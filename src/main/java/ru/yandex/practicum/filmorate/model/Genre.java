package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Класс—модель данных приложения, дающий описание объекту жанр фильма.
 */
@Value
@EqualsAndHashCode(of = {"id"})
@ToString
@RequiredArgsConstructor(staticName = "of")
public class Genre {
    Integer id;
    String name;
}
