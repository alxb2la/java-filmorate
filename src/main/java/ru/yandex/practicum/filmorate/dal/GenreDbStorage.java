package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

/**
 * Класс для взаимодействия объектов Genre с базой данных
 */
@Slf4j
@Repository
@Primary
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?;";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> getAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Genre getGenreById(Integer id) {
        Optional<Genre> optionalGenre = findOne(FIND_BY_ID_QUERY, id);
        return optionalGenre.orElseThrow(() -> new NotFoundException("Жанр c ID: " + id + " не найден"));
    }
}
