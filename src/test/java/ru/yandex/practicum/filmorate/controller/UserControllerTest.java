package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    void testAddCorrectUserAndGetAllUsers() {
        User user = User.of(0, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12));
        User createdUser = userController.addUser(user);

        assertEquals(1, createdUser.getId());
        assertEquals(1, userController.getAllUsers().size());

        user = User.of(0, "User2 name", "Email@mail.com", "qwerty1234",
                LocalDate.of(2000, 12, 12));
        createdUser = userController.addUser(user);

        assertEquals(2, createdUser.getId());
        assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void testUpdateCorrectUser() {
        User user = User.of(0, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12));
        User createdUser = userController.addUser(user);

        user = User.of(createdUser.getId(), "User name111", "Email111@mail.com", "qwerty1234",
                LocalDate.of(1980, 10, 22));
        User updatedUser = userController.updateUser(user);

        assertEquals(1, updatedUser.getId());
        assertEquals("User name111", updatedUser.getName());
        assertEquals("Email111@mail.com", updatedUser.getEmail());
        assertEquals(LocalDate.of(1980, 10, 22), updatedUser.getBirthday());
        assertEquals("qwerty1234", updatedUser.getLogin());
    }

    @Test
    void testNotAddUserWithUncorrectEmail() {
        final User user1 = User.of(0, "User name", "", "qwerty",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user1));

        final User user2 = User.of(0, "User name", "     ", "qwerty",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user2));

        final User user3 = User.of(0, "User name", null, "qwerty",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user3));

        final User user4 = User.of(0, "User name", "qwertymail.com", "qwerty",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user4));
    }

    @Test
    void testNotAddUserWithEmptyLogin() {
        final User user1 = User.of(0, "User name", "123@mail.com", "",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user1));

        final User user2 = User.of(0, "User name", "123@mail.com", "     ",
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user2));

        final User user3 = User.of(0, "User name", "123@mail.com", null,
                LocalDate.of(1990, 12, 12));
        assertThrows(ValidationException.class, () -> userController.addUser(user3));
    }

    @Test
    void testNotAddUserWithDateOfBirthdayInFuture() {
        final User user = User.of(0, "User name", "123@mail.com", "qwerty12345",
                LocalDate.of(2130, 10, 10));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void testAddUserWithEmptyName() {
        User user = User.of(0, "", "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10));
        User createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());

        user = User.of(0, "    ", "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10));
        createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());

        user = User.of(0, null, "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10));
        createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());
    }
}
