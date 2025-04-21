package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * UserController — класс-контроллер, предоставляющий API для работы с данными типа User.
 * Базовый путь - /users.
 * Доступны методы POST, PUT для добавления, обновления пользователя;
 * метод GET для получение списка всех пользователей.
 * Путь /users/{id}.
 * Доступен метод GET для получения пользователя по его ID.
 * Путь /{id}/friends/{friendId}.
 * Доступны методы PUT, DELETE для добавления, удаления друга для пользователя.
 * Путь /{id}/friends
 * Доступен метод GET для получение списка всех друзей пользователя.
 * Путь /{id}/friends/common/{otherId}
 * Доступен метод GET для получение списка общих друзей двух пользователей.
 * Сохраняет корректные объекты User используя реализацию класса UserService.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Запрос на добавление пользователя в приложение");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Запрос на обновление данных пользователя");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя по ID");
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление пользователя в список друзей");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление пользователя из списка друзей");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsById(@PathVariable Long id) {
        log.info("Запрос на получение списка друзей пользователя");
        return userService.getAllFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на получение списка общих друзей пользователей");
        return userService.getAllCommonFriendsByIds(id, otherId);
    }
}
