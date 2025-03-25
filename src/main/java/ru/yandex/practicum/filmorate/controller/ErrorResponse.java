package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс для описания универсального формата ошибки.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String description;
}
