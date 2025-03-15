package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.InMemoryUserManager;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * UserController — класс-контроллер, предоставляющий API для работы с данными типа User.
 * Базовый путь - /users.
 * Доступны методы GET, POST, PUT для добавления, обновления и получения пользователей.
 * Сохраняет корректные объекты User в оперативной памяти.
 * Должен соотвествовать архитектурному стилю проектирования распределённых систем REST
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserManager userManager = new InMemoryUserManager();

    @PostMapping
    public User addUser(@RequestBody User user) {
        return userManager.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userManager.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userManager.getAllUsers();
    }
}
