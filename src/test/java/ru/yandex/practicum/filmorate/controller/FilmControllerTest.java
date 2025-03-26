package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
    }

    @Test
    void testAddCorrectFilmAndGetAllFilms() {
        Film film = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        Film createdFilm = filmController.addFilm(film);

        assertEquals(1, createdFilm.getId());
        assertEquals(1, filmController.getAllFilms().size());

        film = Film.of(0L, "Film name1", "Film description1",
                LocalDate.of(2022, 5, 10), 180, new HashSet<>());
        createdFilm = filmController.addFilm(film);

        assertEquals(2, createdFilm.getId());
        assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void testUpdateCorrectFilm() {
        Film film = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        Film createdFilm = filmController.addFilm(film);

        film = Film.of(createdFilm.getId(), "Film name111", "Film description111",
                LocalDate.of(1990, 10, 20), 120, new HashSet<>());
        Film updatedFilm = filmController.updateFilm(film);

        assertEquals(1, updatedFilm.getId());
        assertEquals("Film name111", updatedFilm.getName());
        assertEquals("Film description111", updatedFilm.getDescription());
        assertEquals(LocalDate.of(1990, 10, 20), updatedFilm.getReleaseDate());
        assertEquals(120, updatedFilm.getDuration());
    }

    @Test
    void testNotAddFilmWithEmptyName() {
        final Film film1 = Film.of(0L, "", "Film description1",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0L, "     ", "Film description2",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film2));

        final Film film3 = Film.of(0L, null, "Film description3",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film3));
    }

    @Test
    void testNotAddFilmWithDescriptionMoreThan200Characters() {
        final Film film1 = Film.of(0L, "Film name1",
                "Too long description Too long description Too long description Too long description" +
                        "Too long description Too long description Too long description Too long description" +
                        "Too long description Too long description Too long description Too long description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        // Boundary value 200 characters
        final Film film2 = Film.of(0L, "Film name2",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value 199 characters
        final Film film3 = Film.of(0L, "Film name3",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptio",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film3));

        // Boundary value 201 characters
        final Film film4 = Film.of(0L, "Film name4",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long description ",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));
    }

    @Test
    void testNotAddFilmWithDateOfReleaseBefore28121895() {
        final Film film1 = Film.of(0L, "Film name1", "Film description",
                LocalDate.of(895, 12, 28), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0L, "Film name2", "Film description",
                LocalDate.of(2020, 10, 20), 120, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value date 1895-12-28
        final Film film3 = Film.of(0L, "Film name3", "Film description",
                LocalDate.of(1895, 12, 28), 120, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film3));

        // Boundary value date 1895-12-27
        final Film film4 = Film.of(0L, "Film name4", "Film description",
                LocalDate.of(1895, 12, 27), 120, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));

        // Boundary value date 1895-12-29
        final Film film5 = Film.of(0L, "Film name5", "Film description",
                LocalDate.of(1895, 12, 29), 120, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film5));
    }

    @Test
    void testNotAddFilmWithZeroOrNegativeDuration() {
        final Film film1 = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), -100, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 90, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value duration 0
        final Film film3 = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 0, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film3));

        // Boundary value duration -1
        final Film film4 = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), -1, new HashSet<>());
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));

        // Boundary value duration 1
        final Film film5 = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 1, new HashSet<>());
        assertDoesNotThrow(() -> filmController.addFilm(film5));
    }

    @Test
    void testGetCorrectFilmById() {
        Film film = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        filmController.addFilm(film);

        film = Film.of(0L, "Film name1", "Film description1",
                LocalDate.of(2022, 5, 10), 180, new HashSet<>());
        filmController.addFilm(film);

        assertEquals("Film name", filmController.getFilmById(1L).getName());
        assertEquals(120, filmController.getFilmById(1L).getDuration());
        assertEquals("Film name1", filmController.getFilmById(2L).getName());
        assertEquals(180, filmController.getFilmById(2L).getDuration());
    }

    @Test
    void testAddLikeByCorrectUserToCorrectFilm() {
        Film film = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        Film createdFilm = filmController.addFilm(film);

        User user = User.of(0L, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        User createdUser = userStorage.addUser(user);

        filmController.addLike(createdFilm.getId(), createdUser.getId());

        assertEquals(1, createdFilm.getLikes().size());
        assertTrue(createdFilm.getLikes().contains(1L));
    }

    @Test
    void testRemoveLikeFromCorrectFilm() {
        Film film = Film.of(0L, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        Film createdFilm = filmController.addFilm(film);

        User user = User.of(0L, "User name", "Email@mail.com", "qwerty",
                LocalDate.of(1990, 12, 12), new HashSet<>());
        User createdUser = userStorage.addUser(user);

        filmController.addLike(createdFilm.getId(), createdUser.getId());
        filmController.removeLike(createdFilm.getId(), createdUser.getId());

        assertEquals(0, createdFilm.getLikes().size());
        assertFalse(createdFilm.getLikes().contains(1L));
    }

    @Test
    void testGetTopFilms() {
        Film film = Film.of(0L, "Film1 name", "Film1 description",
                LocalDate.of(1995, 5, 20), 120, new HashSet<>());
        filmController.addFilm(film);

        film = Film.of(0L, "Film2 name2", "Film2 description2",
                LocalDate.of(2003, 5, 24), 110, new HashSet<>());
        filmController.addFilm(film);

        film = Film.of(0L, "Film3 name3", "Film3 description3",
                LocalDate.of(2002, 1, 4), 100, new HashSet<>());
        filmController.addFilm(film);

        User user = User.of(0L, "User1 name", "Email123@mail.com", "qwerty",
                LocalDate.of(1994, 2, 21), new HashSet<>());
        userStorage.addUser(user);

        user = User.of(0L, "User2 name", "Email321@mail.com", "123",
                LocalDate.of(1998, 7, 12), new HashSet<>());
        userStorage.addUser(user);

        filmController.addLike(3L, 1L);
        filmController.addLike(3L, 2L);
        filmController.addLike(2L, 1L);

        assertEquals(3, filmController.getTopFilms(10).size());
        assertEquals(3, filmController.getTopFilms(10).get(0).getId());
        assertEquals(2, filmController.getTopFilms(10).get(1).getId());
        assertEquals(1, filmController.getTopFilms(10).get(2).getId());
    }
}
