package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(id);
        if (optionalFilm.isEmpty()) {
            log.warn("Не удалось получить объект Film по его ID - не найден в приложении");
            throw new NotFoundException("Фильм c ID: " + id + " не найден");
        }
        log.info("Объект Film успешно найден по ID");
        return optionalFilm.get();
    }

    public void addLike(Long filmId, Long userId) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            log.warn("Не удалось добавить лайк к фильму, объект Film не найден в приложении");
            throw new NotFoundException("Фильм c ID: " + filmId + " не найден");
        }

        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось добавить лайк к фильму, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        boolean isAddingToSet = optionalFilm.get().getLikes().add(optionalUser.get().getId());
        if (isAddingToSet) {
            log.info("Лайк к фильму успешно добавлен");
        } else {
            log.info("Лайк к фильму не добавлен, пользователь может добавить только 1 лайк к фильму");
        }
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            log.warn("Не удалось удалить лайк у фильма, объект Film не найден в приложении");
            throw new NotFoundException("Фильм c ID: " + filmId + " не найден");
        }

        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось удалить лайк у фильма, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        optionalFilm.get().getLikes().remove(optionalUser.get().getId());
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
