package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/**
 * Класс для взаимодействия объектов User с базой данных
 */
@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcOperations jdbc;
    private final RowMapper<User> mapper;


    @Autowired
    public UserDbStorage(final JdbcOperations jdbc, final RowMapper<User> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public User addUser(User user) {
        final String INSERT_USER_QUERY = """
                INSERT INTO users (name, email, login, birthday)
                VALUES (?, ?, ?, ?);
                """;

        final Object[] params = {
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        };

        // insert user
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId == null) {
            throw new InternalServerException("UserDbStorage: Не удалось сохранить данные User");
        }

        return User.of(
                generatedId,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
    }

    @Override
    public User updateUser(User user) {
        final String UPDATE_USER_QUERY = """
                UPDATE users SET name = ?, email = ?, login = ?, birthday = ?
                WHERE id = ?;
                """;

        final Object[] params = {
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        };

        // update user
        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("BaseDbStorage: Не удалось обновить данные User");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        final String FIND_ALL_USERS_QUERY = """
                SELECT *
                FROM users;
                """;
        final String FIND_ALL_USERS_FRIENDS_IDS_QUERY = """
                SELECT *
                FROM user_friend;
                """;

        // get all users without friends ids
        List<User> tmpUsers = jdbc.query(FIND_ALL_USERS_QUERY, mapper);
        if (tmpUsers == null || tmpUsers.isEmpty()) {
            return List.of();
        }
        return tmpUsers;
    }

    public User getUserById(Long id) {
        final String FIND_USER_BY_ID_QUERY = """
                SELECT *
                FROM users
                WHERE id = ?;
                """;

        User user;
        try {
            user = jdbc.queryForObject(FIND_USER_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            user = null;
        }
        if (user == null) {
            log.warn("UserDbStorage: Не удалось получить объект User по его ID - не найден в приложении");
            throw new NotFoundException("UserDbStorage: User c ID: " + id + " не найден в приложении");
        }
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        final String INSERT_USER_FRIEND_QUERY = """
                INSERT INTO user_friend (user_id, friend_id)
                VALUES (?, ?);
                """;

        final Object[] params = {
                userId,
                friendId
        };

        jdbc.update(INSERT_USER_FRIEND_QUERY, params);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        final String DELETE_USER_FRIEND_QUERY = """
                DELETE FROM user_friend
                WHERE user_id = ? AND friend_id = ?;
                """;

        int rowsDeleted = jdbc.update(DELETE_USER_FRIEND_QUERY, userId, friendId);
        if (rowsDeleted == 0) {
            log.info("UserDbStorage: Не удалось удалить друга User с ID: {}", userId);
        }
    }

    @Override
    public List<User> getAllFriendsById(Long userId) {
        final String FIND_USER_FRIENDS_BY_ID_QUERY = """
                SELECT *
                FROM users
                WHERE id IN (SELECT friend_id FROM user_friend WHERE user_id = ?);
                """;

        return jdbc.query(FIND_USER_FRIENDS_BY_ID_QUERY, mapper, userId);
    }

    @Override
    public List<User> getUsersByIdSet(Set<Long> ids) {
        final String FIND_USERS_BY_IDS_QUERY = """
                SELECT *
                FROM users
                WHERE id IN (%s);
                """;
        final String sqlPlaceholders = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbc.query(String.format(FIND_USERS_BY_IDS_QUERY, sqlPlaceholders), mapper, ids.toArray());
    }

    @Override
    public Set<Long> getUserFriendsIdsById(Long userId) {
        final String FIND_USER_FRIENDS_IDS_BY_ID_QUERY = """
                SELECT friend_id
                FROM user_friend
                WHERE user_id = ?;
                """;

        return Set.copyOf(jdbc.queryForList(FIND_USER_FRIENDS_IDS_BY_ID_QUERY, Long.class, userId));
    }
}
