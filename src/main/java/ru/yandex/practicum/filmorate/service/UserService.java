package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
                user.getBirthday()
        );
        return userStorage.addUser(validUser);
    }

    public User updateUser(User user) {
        UserValidation.validate(user);
        if ((user.getId() == null) || (user.getId() < 1L)) {
            log.warn("UserService: Запрос на обновление user с некорректным ID");
            throw new ValidationException("UserService: user не может быть обновлен, ID некорректен " + user.getId());
        }
        // // Check Db
        getUserById(user.getId());

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
                user.getBirthday()
        );
        return userStorage.updateUser(validUser);
    }

    public List<User> getAllUsers() {
        return List.copyOf(userStorage.getAllUsers());
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
        if (userId == null || userId < 1L || friendId == null || friendId < 1L) {
            log.warn("UserService: Запрос на добаление друга, ID некорректен");
            throw new ValidationException("UserService: друг не может быть добален, ID некорректен");
        }
        // check Db
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (!(userFriendsIds.contains(friendId))) {
            userStorage.addFriend(userId, friendId);
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг был добавлен ранее");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || userId < 1L || friendId == null || friendId < 1L) {
            log.warn("UserService: Запрос на удаление друга  ID = null");
            throw new ValidationException("UserService: друг не может быть удален ID = null");
        }
        // check Db
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (userFriendsIds.contains(friendId)) {
            userStorage.removeFriend(userId, friendId);
            log.info("Друг успешно удален");
        } else {
            log.info("друг не может быть удален - отсутствует в списке друзей");
            // postman test требует код ответа 200
            //throw new NotFoundException("Пользователь c ID: " + userId + " не найден в списке друзей");
        }
    }

    public List<User> getAllFriendsById(Long userId) {
        if (userId == null || userId < 1L) {
            log.warn("UserService: Запрос на получение списка друзей по некорректному ID");
            throw new ValidationException("UserService: список друзей не может быть получен по некорректному ID: "
                    + userId);
        }
        // check Db
        userStorage.getUserById(userId);

        List<User> users = userStorage.getAllFriendsById(userId);
        log.info("Список всех друзей пользователя успешно сформирован");
        return List.copyOf(users);
    }

    public List<User> getAllCommonFriendsByIds(Long userId, Long anotherUserId) {
        if (userId == null || userId < 1L || anotherUserId == null || anotherUserId < 1L) {
            log.warn("UserService: Запрос на получение списка общих друзей c некорректным ID");
            throw new ValidationException("UserService: список общих друзей не может быть получен, ID некорректен");
        }
        // check Db
        userStorage.getUserById(userId);
        userStorage.getUserById(anotherUserId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        Set<Long> anotherUserFriendsIds = userStorage.getUserFriendsIdsById(anotherUserId);
        if (userFriendsIds == null || anotherUserFriendsIds == null) {
            log.warn("UserService: Не удалось получить объекты User по ID - не найдены в приложении");
            throw new NotFoundException("UserService: объекты User не найдены в приложении");
        }

        Set<Long> resultOfIntersection = userFriendsIds.stream()
                .filter(anotherUserFriendsIds::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = userStorage.getUsersByIdSet(resultOfIntersection);
        log.info("Список всех общих друзей пользователей успешно сформирован");
        return List.copyOf(commonFriends);
    }
}
