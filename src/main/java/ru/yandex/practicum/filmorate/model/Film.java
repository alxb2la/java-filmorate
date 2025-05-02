package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.SequencedSet;

/**
 * Класс—модель данных приложения, дающий описание объекту фильм.
 */
@Value
@EqualsAndHashCode(of = {"id", "name", "releaseDate"})
@ToString
@RequiredArgsConstructor(staticName = "of")
public class Film {
    Long id;
    String name;
    @ToString.Exclude
    String description;
    LocalDate releaseDate;
    int duration;
    SequencedSet<Genre> genres;
    MpaRating mpa;
}
