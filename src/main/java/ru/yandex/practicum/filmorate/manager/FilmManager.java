package ru.yandex.practicum.filmorate.manager;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс, определяющий набор действий с объектом типа Film в приложении.
 */
public interface FilmManager {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();
}
