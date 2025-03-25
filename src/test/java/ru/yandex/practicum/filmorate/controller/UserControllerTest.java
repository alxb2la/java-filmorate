package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void testAddCorrectUserAndGetAllUsers() {
        User user = User.of(0L, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        User createdUser = userController.addUser(user);

        assertEquals(1, createdUser.getId());
        assertEquals(1, userController.getAllUsers().size());

        user = User.of(0L, "User2 name", "Email@mail.com", "qwerty1234",
                LocalDate.of(2000, 12, 12), new HashSet<>());
        createdUser = userController.addUser(user);

        assertEquals(2, createdUser.getId());
        assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void testUpdateCorrectUser() {
        User user = User.of(0L, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        User createdUser = userController.addUser(user);

        user = User.of(createdUser.getId(), "User name111", "Email111@mail.com", "qwerty1234",
                LocalDate.of(1980, 10, 22), new HashSet<>());
        User updatedUser = userController.updateUser(user);

        assertEquals(1, updatedUser.getId());
        assertEquals("User name111", updatedUser.getName());
        assertEquals("Email111@mail.com", updatedUser.getEmail());
        assertEquals(LocalDate.of(1980, 10, 22), updatedUser.getBirthday());
        assertEquals("qwerty1234", updatedUser.getLogin());
    }

    @Test
    void testNotAddUserWithUncorrectEmail() {
        final User user1 = User.of(0L, "User name", "", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user1));

        final User user2 = User.of(0L, "User name", "     ", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user2));

        final User user3 = User.of(0L, "User name", null, "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user3));

        final User user4 = User.of(0L, "User name", "qwertymail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user4));
    }

    @Test
    void testNotAddUserWithEmptyLogin() {
        final User user1 = User.of(0L, "User name", "123@mail.com", "",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user1));

        final User user2 = User.of(0L, "User name", "123@mail.com", "     ",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user2));

        final User user3 = User.of(0L, "User name", "123@mail.com", null,
                LocalDate.of(1990, 12, 12), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user3));
    }

    @Test
    void testNotAddUserWithDateOfBirthdayInFuture() {
        final User user = User.of(0L, "User name", "123@mail.com", "qwerty12345",
                LocalDate.of(2130, 10, 10), new HashSet<>());
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void testAddUserWithEmptyName() {
        User user = User.of(0L, "", "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10), new HashSet<>());
        User createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());

        user = User.of(0L, "    ", "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10), new HashSet<>());
        createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());

        user = User.of(0L, null, "123@mail.com", "qwerty12345",
                LocalDate.of(2000, 10, 10), new HashSet<>());
        createdUser = userController.addUser(user);
        assertEquals("qwerty12345", createdUser.getName());
    }

    @Test
    void testGetCorrectUserById() {
        User user = User.of(0L, "User1 name", "Email123@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User2 name", "Email321@mail.com", "qwerty123",
                LocalDate.of(1994, 2, 2), new HashSet<>());
        userController.addUser(user);

        assertEquals("User1 name", userController.getUserById(1L).getName());
        assertEquals("Email123@mail.com", userController.getUserById(1L).getEmail());
        assertEquals("User2 name", userController.getUserById(2L).getName());
        assertEquals("Email321@mail.com", userController.getUserById(2L).getEmail());
    }

    @Test
    void testAddCorrectFriendUserByIdAndGetAllFriends() {
        User user = User.of(0L, "User1 name", "Email123@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User2 name", "Email321@mail.com", "qwerty123",
                LocalDate.of(1994, 2, 2), new HashSet<>());
        userController.addUser(user);

        userController.addFriendById(1L, 2L);

        assertEquals(1, userController.getAllFriendsById(1L).size());
        assertEquals(2, userController.getAllFriendsById(1L).get(0).getId());
        assertEquals(1, userController.getAllFriendsById(2L).size());
        assertEquals(1, userController.getAllFriendsById(2L).get(0).getId());
    }

    @Test
    void testRemoveCorrectFriendFromUserById() {
        User user = User.of(0L, "User1 name", "Email123@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User2 name", "Email321@mail.com", "qwerty123",
                LocalDate.of(1994, 2, 2), new HashSet<>());
        userController.addUser(user);

        userController.addFriendById(1L, 2L);
        userController.removeFriendById(1L, 2L);

        assertEquals(0, userController.getAllFriendsById(1L).size());
        assertEquals(0, userController.getAllFriendsById(2L).size());
    }

    @Test
    void testGetCommonFriendsOfUsersByIds() {
        User user = User.of(0L, "User1 name", "Email123@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User2 name", "Email321@mail.com", "qwerty123",
                LocalDate.of(1994, 2, 2), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User3 name", "Email3221@mail.com", "123qwerty123",
                LocalDate.of(1996, 2, 2), new HashSet<>());
        userController.addUser(user);

        user = User.of(0L, "User4 name", "Email324421@mail.com", "4123qwerty1234",
                LocalDate.of(2001, 2, 23), new HashSet<>());
        userController.addUser(user);

        userController.addFriendById(1L, 2L);
        userController.addFriendById(1L, 3L);

        userController.addFriendById(4L, 3L);
        userController.addFriendById(4L, 2L);

        assertEquals(2, userController.getAllCommonFriendsByIds(1L, 4L).size());
        assertEquals(2, userController.getAllCommonFriendsByIds(1L, 4L).get(0).getId());
        assertEquals(3, userController.getAllCommonFriendsByIds(1L, 4L).get(1).getId());
    }
}
