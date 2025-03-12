package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.manager.InMemoryFilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * FilmController — класс-контроллер, предоставляющий API для работы с данными типа Film.
 * Базовый путь - /films.
 * Доступны методы GET, POST, PUT для добавления, обновления и получения фильмов.
 * Сохраняет корректные объекты Film в оперативной памяти.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmManager filmManager = new InMemoryFilmManager();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmManager.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmManager.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmManager.getAllFilms();
    }
}
