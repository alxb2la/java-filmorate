package ru.yandex.practicum.filmorate.model;

import java.util.List;

public enum GenreValue {
    COMEDY(GenreType.consts.get(0)),
    DRAMA(GenreType.consts.get(1)),
    CARTOON(GenreType.consts.get(2)),
    THRILLER(GenreType.consts.get(3)),
    DOCUMENTARY(GenreType.consts.get(4)),
    ACTION(GenreType.consts.get(5));

    private final String value;

    private GenreValue(String v) {
        value = v;
    }

    public String getVal() {
        return value;
    }

    public static boolean isCorrect(String testedValue) {
        return GenreType.consts.contains(testedValue);
    }

    private static class GenreType {
        public static final List<String> consts = List.of("Комедия", "Драма", "Мультфильм", "Триллер",
                "Документальный", "Боевик");
    }
}
