package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

/**
 * MpaRatingController — класс-контроллер, предоставляющий API для работы с данными типа MpaRating.
 * Базовый путь - /mpa.
 * Доступен метод GET для получение списка всех рейтингов для фильмов в приложении.
 * Путь /mpa.
 * Доступен метод GET для получения рейтинга по его ID.
 * Путь /mpa/{id}.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaRatingController {
    private final MpaRatingService mpaRatingService;

    @Autowired
    public MpaRatingController(MpaRatingService mpaRatingService) {
        this.mpaRatingService = mpaRatingService;
    }

    @GetMapping
    public List<MpaRating> getAllMpaRatings() {
        log.info("Запрос на получение списка всех рейтингов");
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@PathVariable Integer id) {
        log.info("Запрос на получение рейтинга по ID");
        return mpaRatingService.getMpaRatingById(id);
    }
}
