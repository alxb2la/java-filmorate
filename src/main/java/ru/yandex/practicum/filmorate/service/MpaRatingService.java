package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.*;

/**
 * MpaRatingService — класс, который отвечает за такие операции с рейтингами как
 * получение списка всех рейтингов, получение объекта рейтинг по его ID.
 * Является компонентом фреймворка Spring boot
 */
@Service
@Slf4j
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    @Autowired
    public MpaRatingService(MpaRatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Integer id) {
        if (id == null) {
            log.warn("Запрос на получение рейтинга по ID = null");
            throw new ValidationException("Рейтинг не может быть получен по ID = null");
        }
        return mpaRatingStorage.getMpaRatingById(id);
    }
}
