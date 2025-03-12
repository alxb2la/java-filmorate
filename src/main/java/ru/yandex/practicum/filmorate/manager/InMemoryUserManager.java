package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс предоставляет реализацию интерфейса UserManager
 * и хранит объекты User в оперативной памяти.
 */
@Slf4j
public class InMemoryUserManager implements UserManager {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        UserValidation.validate(user);
        User innerCopyUser;
        // Замена пустого имени на логин
        if (user.getName() == null || user.getName().isBlank()) {
            innerCopyUser = User.of(getNextId(), user.getLogin(), user.getEmail(),
                    user.getLogin(), user.getBirthday());
        } else {
            innerCopyUser = User.of(getNextId(), user.getName(), user.getEmail(),
                    user.getLogin(), user.getBirthday());
        }
        users.put(innerCopyUser.getId(), innerCopyUser);
        log.info("Объект User успешно добавлен");
        return innerCopyUser;
    }

    @Override
    public User updateUser(User user) {
        UserValidation.validate(user);
        if (!users.containsKey(user.getId())) {
            log.warn("Не удалось обновить объект User - не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + user.getId() + " не найден");
        }
        User innerCopyUser;
        // Замена пустого имени на логин
        if (user.getName() == null || user.getName().isBlank()) {
            innerCopyUser = User.of(user.getId(), user.getLogin(), user.getEmail(),
                    user.getLogin(), user.getBirthday());
        } else {
            innerCopyUser = User.of(user.getId(), user.getName(), user.getEmail(),
                    user.getLogin(), user.getBirthday());
        }
        users.put(innerCopyUser.getId(), innerCopyUser);
        log.info("Объект Film успешно обновлен");
        return innerCopyUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(List.copyOf(users.values()));
    }

    // Вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
