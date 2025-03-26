package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        Optional<User> optionalUser = userStorage.getUserById(id);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось получить объект User по его ID - не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + id + " не найден");
        }
        log.info("Объект User успешно найден по ID");
        return optionalUser.get();
    }

    public void addFriendById(Long userId, Long friendId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось добавить друга, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        Optional<User> optionalUserFriend = userStorage.getUserById(friendId);
        if (optionalUserFriend.isEmpty()) {
            log.warn("Не удалось добавить друга, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + friendId + " не найден");
        }

        boolean isAdding1stFriend = optionalUser.get().getFriends().add(optionalUserFriend.get().getId());
        boolean isAdding2ndFriend = optionalUserFriend.get().getFriends().add(optionalUser.get().getId());
        if (isAdding1stFriend && isAdding2ndFriend) {
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг уже был добавлен раньше");
        }
    }

    public void removeFriendById(Long userId, Long friendId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось удалить друга, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        Optional<User> optionalUserFriend = userStorage.getUserById(friendId);
        if (optionalUserFriend.isEmpty()) {
            log.warn("Не удалось удалить друга, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + friendId + " не найден");
        }

        optionalUser.get().getFriends().remove(optionalUserFriend.get().getId());
        optionalUserFriend.get().getFriends().remove(optionalUser.get().getId());
        log.info("Друг успешно удален");
    }

    public List<User> getAllFriendsById(Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось получить список друзей, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        log.info("Список всех друзей пользователя успешно сформирован");
        List<User> users = new ArrayList<>();
        for (Long friendId : optionalUser.get().getFriends()) {
            Optional<User> optionalFriend = userStorage.getUserById(friendId);
            if (optionalFriend.isPresent()) {
                users.add(optionalFriend.get());
            }
        }
        return users;
    }

    public List<User> getAllCommonFriendsByIds(Long userId, Long anotherUserId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("Не удалось получить список общих друзей, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        Optional<User> optionalAnotherUser = userStorage.getUserById(anotherUserId);
        if (optionalAnotherUser.isEmpty()) {
            log.warn("Не удалось получить список общих друзей, объект User не найден в приложении");
            throw new NotFoundException("Пользователь c ID: " + anotherUserId + " не найден");
        }

        Set<Long> resultOfIntersection = optionalUser.get().getFriends()
                .stream()
                .filter(optionalAnotherUser.get().getFriends()::contains)
                .collect(Collectors.toSet());

        log.info("Список всех общих друзей пользователей успешно сформирован");
        List<User> users = new ArrayList<>();
        for (Long friendId : resultOfIntersection) {
            Optional<User> optionalFriend = userStorage.getUserById(friendId);
            if (optionalFriend.isPresent()) {
                users.add(optionalFriend.get());
            }
        }
        return users;
    }
}
