package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * FilmController — класс-контроллер, предоставляющий API для работы с данными типа Film.
 * Базовый путь - /films.
 * Доступны методы POST, PUT для добавления, обновления фильма;
 * метод GET для получение списка всех фильмов.
 * Путь /films/{id}.
 * Доступен метод GET для получения фильма по его ID.
 * Путь /films/{id}/like/{userId}
 * Доступны методы PUT, DELETE для добавления, удаления лайка к фильму.
 * Путь /films/popular?count={count}.
 * Доступен метод GET для получения списка из первых count фильмов по количеству лайков.
 * Если значение параметра count не задано, возвращается первые 10 фильмов
 * Сохраняет корректные объекты Film используя реализацию класса FilmService.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }
}
