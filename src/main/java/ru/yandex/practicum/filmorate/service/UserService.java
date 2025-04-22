package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserService — класс, который отвечает за такие операции с пользователями, как добавление в друзья,
 * удаление из друзей, вывод списка общих друзей.
 * Является компонентом фреймворка Spring boot
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        UserValidation.validate(user);
        // Проверка поля friends
        Set<Long> validFriends;
        if (user.getFriends() == null) {
            validFriends = Set.of();
        } else {
            validFriends = user.getFriends();
        }
        // Замена пустого имени на логин
        String validName;
        if (user.getName() == null || user.getName().isBlank()) {
            validName = user.getLogin();
        } else {
            validName = user.getName();
        }

        User validUser = User.of(
                user.getId(),
                validName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                validFriends
        );
        return userStorage.addUser(validUser);
    }

    public User updateUser(User user) {
        UserValidation.validate(user);
        // Проверка наличия user в бд
        getUserById(user.getId());
        // Проверка поля friends
        Set<Long> validFriends;
        if (user.getFriends() == null) {
            validFriends = Set.of();
        } else {
            validFriends = user.getFriends();
        }
        // Замена пустого имени на логин
        String validName;
        if (user.getName() == null || user.getName().isBlank()) {
            validName = user.getLogin();
        } else {
            validName = user.getName();
        }

        User validUser = User.of(
                user.getId(),
                validName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                validFriends
        );
        return userStorage.updateUser(validUser);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        if (id == null) {
            log.warn("UserService: Запрос на получение user по ID = null");
            throw new ValidationException("UserService: user не может быть получен по ID = null");
        }
        User user = userStorage.getUserById(id);
        log.info("UserService: Объект user успешно найден по ID");
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("UserService: Запрос на добаление друга - ID = null");
            throw new ValidationException("UserService: друг не может быть добален ID = null");
        }
        // Проверка наличия объектов в бд
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!(user.getFriends().contains(friendId))) {
            userStorage.addFriend(userId, friendId);
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг был добавлен ранее");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            log.warn("UserService: Запрос на удаление друга  ID = null");
            throw new ValidationException("UserService: друг не может быть удален ID = null");
        }
        // Проверка наличия объектов в бд
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            userStorage.removeFriend(userId, friendId);
            log.info("Друг успешно удален");
        } else {
            log.info("друг не может быть удален - отсутствует в списке");
        }
    }

    public List<User> getAllFriendsById(Long userId) {
        if (userId == null) {
            log.warn("UserService: Запрос на получение списка друзей по ID = null");
            throw new ValidationException("UserService: список друзей не может быть получен по ID = null");
        }
        // Проверка наличия объектов в бд
        userStorage.getUserById(userId);

        List<User> users = userStorage.getAllFriendsById(userId);
        log.info("Список всех друзей пользователя успешно сформирован");
        return List.copyOf(users);
    }

    public List<User> getAllCommonFriendsByIds(Long userId, Long anotherUserId) {
        if (userId == null || anotherUserId == null) {
            log.warn("UserService: Запрос на получение списка общих друзей ID = null");
            throw new ValidationException("UserService: список общих друзей не может быть получен ID = null");
        }
        // Проверка наличия объектов в бд
        User user = userStorage.getUserById(userId);
        User anotherUser = userStorage.getUserById(anotherUserId);

        Set<Long> resultOfIntersection = user.getFriends()
                .stream()
                .filter(anotherUser.getFriends()::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : resultOfIntersection) {
            commonFriends.add(userStorage.getUserById(friendId));
        }
        log.info("Список всех общих друзей пользователей успешно сформирован");
        return List.copyOf(commonFriends);
    }
}
