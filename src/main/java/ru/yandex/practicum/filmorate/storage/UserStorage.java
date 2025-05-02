package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс, определяющий набор действий хранения и получения с объектом типа User в приложении.
 */
public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getAllFriendsById(Long userId);

    List<User> getUsersByIdSet(Set<Long> ids);

    Set<Long> getUserFriendsIdsById(Long userId);
}
