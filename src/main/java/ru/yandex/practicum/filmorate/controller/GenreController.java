package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

/**
 * GenreController — класс-контроллер, предоставляющий API для работы с данными типа Genre.
 * Базовый путь - /genres.
 * Доступен метод GET для получение списка всех жанров в приложении.
 * Путь /genres.
 * Доступен метод GET для получения жанра по его ID.
 * Путь /genres/{id}.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Запрос на получение списка всех жанров");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("Запрос на получение жанра по ID");
        return genreService.getGenreById(id);
    }
}
