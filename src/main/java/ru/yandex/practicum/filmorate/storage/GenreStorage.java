package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * Интерфейс, определяющий набор действий хранения и получения с объектом типа Genre в приложении.
 */
public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenreById(Integer genreId);
}
