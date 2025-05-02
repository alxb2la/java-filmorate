package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Класс mapper данных типа Film
 */
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer ratingId = rs.getInt("mpa_rating_id");
        if (ratingId == 0) {
            ratingId = null;
        }
        MpaRating mpa = MpaRating.of(
                ratingId,
                rs.getString("mpa_rating_name")
        );

        return Film.of(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                mpa
        );
    }
}
