package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

/**
 * Интерфейс, определяющий набор действий хранения и получения с объектом типа MpaRating в приложении.
 */
public interface MpaRatingStorage {
    List<MpaRating> getAllMpaRatings();

    MpaRating getMpaRatingById(Integer mpaId);
}
