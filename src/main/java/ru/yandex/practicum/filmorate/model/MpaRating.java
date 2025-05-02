package ru.yandex.practicum.filmorate.model;

import lombok.*;

/**
 * Класс—модель данных приложения, дающий описание объекту рейтинг фильма.
 */
@Value
@EqualsAndHashCode(of = {"id"})
@ToString
@RequiredArgsConstructor(staticName = "of")
public class MpaRating {
    Integer id;
    String name;
}
