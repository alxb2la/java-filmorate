package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;

/**
 * Класс предоставляет реализацию интерфейса UserStorage
 * и хранит объекты User в оперативной памяти.
 */
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        UserValidation.validate(user);
        User innerCopyUser;
        // Замена пустого имени на логин
        if (user.getName() == null || user.getName().isBlank()) {
            innerCopyUser = User.of(getNextId(), user.getLogin(), user.getEmail(),
                    user.getLogin(), user.getBirthday(), new HashSet<>());
        } else {
            innerCopyUser = User.of(getNextId(), user.getName(), user.getEmail(),
                    user.getLogin(), user.getBirthday(), new HashSet<>());
        }
        users.put(innerCopyUser.getId(), innerCopyUser);
        log.info("Объект User успешно добавлен");
        return innerCopyUser;
    }

    @Override
    public User updateUser(User user) {
        UserValidation.validate(user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Не удалось обновить объект User - не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + user.getId() + " не найден");
        }

        // Проверка поля friends, если null, то использовать empty set
        Set<Long> innerFriends;
        if (user.getFriends() == null) {
            innerFriends = new HashSet<>();
        } else {
            innerFriends = user.getFriends();
        }

        // Замена пустого имени на логин
        User innerCopyUser;
        if (user.getName() == null || user.getName().isBlank()) {
            innerCopyUser = User.of(user.getId(), user.getLogin(), user.getEmail(),
                    user.getLogin(), user.getBirthday(), innerFriends);
        } else {
            innerCopyUser = User.of(user.getId(), user.getName(), user.getEmail(),
                    user.getLogin(), user.getBirthday(), innerFriends);
        }

        users.put(innerCopyUser.getId(), innerCopyUser);
        log.info("Объект Film успешно обновлен");
        return innerCopyUser;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> collectedUsers = new ArrayList<>(List.copyOf(users.values()));
        log.info("Список всех объектов User успешно сформирован");
        return collectedUsers;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(users.get(userId));
        }
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
