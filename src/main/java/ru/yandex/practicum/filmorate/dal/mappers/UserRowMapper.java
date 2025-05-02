package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс mapper данных типа User
 */
@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.of(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
