package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.*;

/**
 * Класс предоставляет реализацию интерфейса FilmManager
 * и хранит объекты Film в оперативной памяти.
 */
@Slf4j
public class InMemoryFilmManager implements FilmManager {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        FilmValidation.validate(film);
        Film innerCopyFilm = Film.of(getNextId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        films.put(innerCopyFilm.getId(), innerCopyFilm);
        log.info("Объект Film успешно добавлен");
        return innerCopyFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidation.validate(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Не удалось обновить объект Film - не найден в приложении");
            throw new NotFoundException("Фильм c ID: " + film.getId() + " не найден");
        }
        Film innerCopyFilm = Film.of(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        films.put(innerCopyFilm.getId(), innerCopyFilm);
        log.info("Объект Film успешно обновлен");
        return innerCopyFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(List.copyOf(films.values()));
    }

    // Вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
