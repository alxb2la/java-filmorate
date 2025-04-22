package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

/**
 * Класс для взаимодействия объектов Film с базой данных
 */
@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final MpaRatingService mpaRatingService;

    private static final String INSERT_FILM_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) " +
            "VALUES (?, ?);";
    private static final String INSERT_FILM_LIKE_QUERY = "INSERT INTO film_like (film_id, user_id) " +
            "VALUES (?, ?);";

    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_rating_id = ? WHERE id = ?;";

    private static final String DELETE_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?;";
    private static final String DELETE_FILM_LIKE_QUERY = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?;";
    private static final String DELETE_ALL_FILM_LIKES_QUERY = "DELETE FROM film_like WHERE film_id = ?;";

    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?;";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT * FROM films;";
    private static final String FIND_FILM_GENRES_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id IN " +
            "(SELECT genre_id FROM film_genre WHERE film_id = ?);";
    private static final String FIND_FILM_LIKES_BY_ID_QUERY = "SELECT user_id FROM film_like WHERE film_id = ?;";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaRatingService mpaRatingService) {
        super(jdbc, mapper);
        this.mpaRatingService = mpaRatingService;
    }

    @Override
    public Film addFilm(Film film) {
        Long genId = insertAndGetKey(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        // add genres
        if (!(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                insertAndNotGetKey(INSERT_FILM_GENRE_QUERY, genId, genre.getId());
            }
        }
        return Film.of(
                genId,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikes(),
                film.getGenres(),
                film.getMpa()
        );
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        // update genres
        delete(DELETE_GENRE_QUERY, film.getId());
        if (!(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                insertAndNotGetKey(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
            }
        }
        // update likes
        delete(DELETE_ALL_FILM_LIKES_QUERY, film.getId());
        if (!(film.getLikes().isEmpty())) {
            for (Long userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> rowFilms = findMany(FIND_ALL_FILMS_QUERY);
        List<Film> films = new ArrayList<>();

        for (Film rowFilm : rowFilms) {
            Film film = Film.of(
                    rowFilm.getId(),
                    rowFilm.getName(),
                    rowFilm.getDescription(),
                    rowFilm.getReleaseDate(),
                    rowFilm.getDuration(),
                    getFilmLikesById(rowFilm.getId()),
                    getFilmGenresById(rowFilm.getId()),
                    mpaRatingService.getMpaRatingById(rowFilm.getMpa().getId())
            );
            films.add(film);
        }
        return List.copyOf(films);
    }

    @Override
    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = findOne(FIND_FILM_BY_ID_QUERY, id);
        if (optionalFilm.isEmpty()) {
            log.warn("FilmDbStorage: Не удалось получить объект Film по его ID - не найден в приложении");
            throw new NotFoundException("FilmDbStorage: Фильм c ID: " + id + " не найден");
        }
        Film rowFilm = optionalFilm.get();
        return Film.of(
                rowFilm.getId(),
                rowFilm.getName(),
                rowFilm.getDescription(),
                rowFilm.getReleaseDate(),
                rowFilm.getDuration(),
                getFilmLikesById(rowFilm.getId()),
                getFilmGenresById(rowFilm.getId()),
                mpaRatingService.getMpaRatingById(rowFilm.getMpa().getId())
        );
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        insertAndNotGetKey(INSERT_FILM_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        delete(DELETE_FILM_LIKE_QUERY, filmId, userId);
    }

    private Set<Genre> getFilmGenresById(Long filmId) {
        List<Genre> genres = jdbc.query(FIND_FILM_GENRES_BY_ID_QUERY, (rs, rowNum) ->
                Genre.of(rs.getInt("genre_id"), rs.getString("name")), filmId);
        return Collections.unmodifiableSet(new LinkedHashSet<>(genres));
    }

    private Set<Long> getFilmLikesById(Long filmId) {
        List<Long> likes = findManyLong(FIND_FILM_LIKES_BY_ID_QUERY, filmId);
        return Set.copyOf(likes);
    }
}
