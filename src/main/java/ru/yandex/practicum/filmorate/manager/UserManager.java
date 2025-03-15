package ru.yandex.practicum.filmorate.manager;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс, определяющий набор действий с объектом типа User в приложении.
 */
public interface UserManager {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();
}
