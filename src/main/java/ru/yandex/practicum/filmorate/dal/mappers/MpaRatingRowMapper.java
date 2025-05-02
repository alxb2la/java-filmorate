package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс mapper данных типа MpaRating
 */
@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating> {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer mpaId = rs.getInt("mpa_rating_id");
        if (mpaId == 0) {
            mpaId = null;
        }
        return MpaRating.of(
                mpaId,
                rs.getString("name")
        );
    }
}
