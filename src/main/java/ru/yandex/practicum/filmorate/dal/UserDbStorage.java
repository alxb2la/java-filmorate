package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

/**
 * Класс для взаимодействия объектов User с базой данных
 */
@Slf4j
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String INSERT_USER_QUERY = "INSERT INTO users (name, email, login, birthday)" +
            " VALUES (?, ?, ?, ?);";
    private static final String INSERT_USER_FRIEND_QUERY = "INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?);";

    private static final String UPDATE_USER_QUERY = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ?" +
            " WHERE id = ?;";

    private static final String DELETE_ALL_USER_FRIENDS_QUERY = "DELETE FROM user_friend WHERE user_id = ?;";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?;";

    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users;";
    private static final String FIND_USER_FRIENDS_ID_BY_ID_QUERY = "SELECT friend_id FROM user_friend WHERE user_id = ?;";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?;";
    private static final String FIND_USER_FRIENDS_BY_ID_QUERY = "SELECT * FROM users WHERE id IN " +
            "(SELECT friend_id FROM user_friend WHERE user_id = ?);";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        Long genId = insertAndGetKey(INSERT_USER_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return User.of(
                genId,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getFriends()
        );
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_USER_QUERY,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        );
        // update friends
        delete(DELETE_ALL_USER_FRIENDS_QUERY, user.getId());
        if (!(user.getFriends().isEmpty())) {
            for (Long friendId : user.getFriends()) {
                addFriend(user.getId(), friendId);
            }
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> rowUsers = findMany(FIND_ALL_USERS_QUERY);
        List<User> users = new ArrayList<>();

        for (User rowUser : rowUsers) {
            User user = User.of(
                    rowUser.getId(),
                    rowUser.getName(),
                    rowUser.getEmail(),
                    rowUser.getLogin(),
                    rowUser.getBirthday(),
                    getUserFriendsIdById(rowUser.getId())
            );
            users.add(user);
        }
        return List.copyOf(users);
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalUser = findOne(FIND_USER_BY_ID_QUERY, id);
        if (optionalUser.isEmpty()) {
            log.warn("UserDbStorage: Не удалось получить объект User по его ID - не найден в приложении");
            throw new NotFoundException("UserDbStorage: User c ID: " + id + " не найден");
        }
        User rowUser = optionalUser.get();
        return User.of(
                rowUser.getId(),
                rowUser.getName(),
                rowUser.getEmail(),
                rowUser.getLogin(),
                rowUser.getBirthday(),
                getUserFriendsIdById(rowUser.getId())
        );
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        insertAndNotGetKey(INSERT_USER_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        delete(DELETE_USER_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> getAllFriendsById(Long userId) {
        return findMany(FIND_USER_FRIENDS_BY_ID_QUERY, userId);
    }

    private Set<Long> getUserFriendsIdById(Long userId) {
        return Set.copyOf(findManyLong(FIND_USER_FRIENDS_ID_BY_ID_QUERY, userId));
    }
}
