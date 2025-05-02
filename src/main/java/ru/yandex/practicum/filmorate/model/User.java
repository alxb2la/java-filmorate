package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;

/**
 * Класс—модель данных приложения, дающий описание объекту пользователь.
 */
@Value
@EqualsAndHashCode(of = {"id", "name", "email", "login"})
@ToString
@RequiredArgsConstructor(staticName = "of")
public class User {
    Long id;
    String name;
    String email;
    String login;
    LocalDate birthday;
}
