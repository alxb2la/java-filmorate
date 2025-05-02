package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс mapper данных типа Genre
 */
@Component
public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer genreId = rs.getInt("genre_id");
        if (genreId == 0) {
            genreId = null;
        }
        return Genre.of(
                genreId,
                rs.getString("name")
        );
    }
}
