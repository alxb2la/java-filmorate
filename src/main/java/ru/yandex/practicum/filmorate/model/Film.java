package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

/**
 * Класс—модель данных приложения, дающий описание объекту фильм.
 */
@Value
@EqualsAndHashCode(of = {"id", "name", "releaseDate"})
@ToString
@RequiredArgsConstructor(staticName = "of")
public class Film {
    long id;
    String name;
    @ToString.Exclude
    String description;
    LocalDate releaseDate;
    int duration;
}
