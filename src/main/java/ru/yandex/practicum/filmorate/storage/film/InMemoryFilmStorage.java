package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.*;

/**
 * Класс предоставляет реализацию интерфейса FilmStorage
 * и хранит объекты Film в оперативной памяти.
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        FilmValidation.validate(film);
        Film innerCopyFilm = Film.of(getNextId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), new HashSet<>());
        films.put(innerCopyFilm.getId(), innerCopyFilm);
        log.info("Объект Film успешно добавлен");
        return innerCopyFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        FilmValidation.validate(film);
        // Проверка поля id и объекта в map по этому id
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Не удалось обновить объект Film - не найден в приложении");
            throw new NotFoundException("Фильм c ID: " + film.getId() + " не найден");
        }
        Film innerCopyFilm;
        // Проверка поля likes, если null, то использовать empty set
        if (film.getLikes() == null) {
            innerCopyFilm = Film.of(film.getId(), film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), new HashSet<>());
        } else {
            innerCopyFilm = Film.of(film.getId(), film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getLikes());
        }
        films.put(innerCopyFilm.getId(), innerCopyFilm);
        log.info("Объект Film успешно обновлен");
        return innerCopyFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> collectedFilms = new ArrayList<>(List.copyOf(films.values()));
        log.info("Список всех объектов Film успешно сформирован");
        return collectedFilms;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        if (filmId == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(films.get(filmId));
        }
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
