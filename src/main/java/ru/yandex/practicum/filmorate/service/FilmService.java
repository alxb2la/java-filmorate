package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FilmService — класс, который отвечает за операции с фильмами — добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков.
 * Является компонентом фреймворка Spring boot
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        FilmValidation.validate(film);
        // Проверка полей likes, genres, mpaRating
        Set<Long> validLikes;
        if (film.getLikes() == null) {
            validLikes = Set.of();
        } else {
            validLikes = film.getLikes();
        }

        Set<Genre> validGenres = new LinkedHashSet<>();
        if (film.getGenres() == null) {
            validGenres = Set.of();
        } else {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValue.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValue.isCorrect(genre.getName()))) {
                    validGenres.add(genre);
                } else {
                    Genre validGenre = Genre.of(genre.getId(), GenreValue.values()[genre.getId() - 1].getVal());
                    validGenres.add(validGenre);
                }
            }
        }

        MpaRating validMpaRating;
        if (film.getMpa() == null) {
            validMpaRating = MpaRating.of(1, MpaRatingValue.values()[0].getVal());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > MpaRatingValue.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (MpaRatingValue.isCorrect(film.getMpa().getName()))) {
                validMpaRating = film.getMpa();
            } else {
                validMpaRating = MpaRating.of(film.getMpa().getId(),
                        MpaRatingValue.values()[film.getMpa().getId() - 1].getVal());
            }
        }

        Film validFilm = Film.of(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                validLikes,
                Collections.unmodifiableSet(validGenres),
                validMpaRating
        );
        return filmStorage.addFilm(validFilm);
    }

    public Film updateFilm(Film film) {
        FilmValidation.validate(film);
        // Проверка наличия фильма в бд
        getFilmById(film.getId());
        // Проверка полей likes, genres, mpaRating
        Set<Long> validLikes;
        if (film.getLikes() == null) {
            validLikes = Set.of();
        } else {
            validLikes = film.getLikes();
        }

        Set<Genre> validGenres = new LinkedHashSet<>();
        if (film.getGenres() == null) {
            validGenres = Set.of();
        } else {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValue.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValue.isCorrect(genre.getName()))) {
                    validGenres.add(genre);
                } else {
                    Genre validGenre = Genre.of(genre.getId(), GenreValue.values()[genre.getId() - 1].getVal());
                    validGenres.add(validGenre);
                }
            }
        }

        MpaRating validMpaRating;
        if (film.getMpa() == null) {
            validMpaRating = MpaRating.of(1, MpaRatingValue.values()[0].getVal());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > MpaRatingValue.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (MpaRatingValue.isCorrect(film.getMpa().getName()))) {
                validMpaRating = film.getMpa();
            } else {
                validMpaRating = MpaRating.of(film.getMpa().getId(),
                        MpaRatingValue.values()[film.getMpa().getId() - 1].getVal());
            }
        }

        Film validFilm = Film.of(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                validLikes,
                Collections.unmodifiableSet(validGenres),
                validMpaRating
        );
        return filmStorage.updateFilm(validFilm);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        if (id == null) {
            log.warn("FilmService: Запрос на получение фильма по ID = null");
            throw new ValidationException("FilmService: Фильм не может быть получен по ID = null");
        }
        Film film = filmStorage.getFilmById(id);
        log.info("FilmService: Объект Film успешно найден по ID");
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmId == null) {
            log.warn("FilmService: Запрос на добаление лайка к фильму по ID фильма = null");
            throw new ValidationException("FilmService: Лайк к фильму не может быть добален по ID фильма = null");
        }
        if (userId == null) {
            log.warn("FilmService: Запрос на добаление лайка к фильму по ID пользователя = null");
            throw new ValidationException("FilmService: Лайк к фильму не может быть добален по ID пользователя = null");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Лайк к фильму успешно добавлен");
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null) {
            log.warn("FilmService: Запрос на удаление лайка у фильма по ID фильма = null");
            throw new ValidationException("FilmService: Лайк у фильма не может быть удален по ID фильма = null");
        }
        if (userId == null) {
            log.warn("FilmService: Запрос на удаление лайка у фильма по ID пользователя = null");
            throw new ValidationException("FilmService: Лайк у фильма не может быть удален по ID пользователя = null");
        }
        filmStorage.removeLike(filmId, userId);
        log.info("Лайк у фильма успешно удален");
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparing(Film::getLikes, (s1, s2) -> Integer.compare(s2.size(), s1.size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
