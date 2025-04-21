package ru.yandex.practicum.filmorate.model;

import java.util.List;

public enum MpaRatingValue {
    G(MpaTypes.consts.get(0)),
    PG(MpaTypes.consts.get(1)),
    PG13(MpaTypes.consts.get(2)),
    R(MpaTypes.consts.get(3)),
    NC17(MpaTypes.consts.get(4));

    private final String value;

    private MpaRatingValue(String v) {
        value = v;
    }

    public String getVal() {
        return value;
    }

    public static boolean isCorrect(String testedValue) {
        return MpaTypes.consts.contains(testedValue);
    }

    private static class MpaTypes {
        public static final List<String> consts = List.of("G", "PG", "PG-13", "R", "NC-17");
    }
}
