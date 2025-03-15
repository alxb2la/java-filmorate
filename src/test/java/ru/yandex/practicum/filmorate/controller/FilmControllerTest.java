package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void testAddCorrectFilmAndGetAllFilms() {
        Film film = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120);
        Film createdFilm = filmController.addFilm(film);

        assertEquals(1, createdFilm.getId());
        assertEquals(1, filmController.getAllFilms().size());

        film = Film.of(0, "Film name1", "Film description1",
                LocalDate.of(2022, 5, 10), 180);
        createdFilm = filmController.addFilm(film);

        assertEquals(2, createdFilm.getId());
        assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void testUpdateCorrectFilm() {
        Film film = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 120);
        Film createdFilm = filmController.addFilm(film);

        film = Film.of(createdFilm.getId(), "Film name111", "Film description111",
                LocalDate.of(1990, 10, 20), 120);
        Film updatedFilm = filmController.updateFilm(film);

        assertEquals(1, updatedFilm.getId());
        assertEquals("Film name111", updatedFilm.getName());
        assertEquals("Film description111", updatedFilm.getDescription());
        assertEquals(LocalDate.of(1990, 10, 20), updatedFilm.getReleaseDate());
        assertEquals(120, updatedFilm.getDuration());
    }

    @Test
    void testNotAddFilmWithEmptyName() {
        final Film film1 = Film.of(0, "", "Film description1",
                LocalDate.of(1995, 5, 20), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0, "     ", "Film description2",
                LocalDate.of(1995, 5, 20), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film2));

        final Film film3 = Film.of(0, null, "Film description3",
                LocalDate.of(1995, 5, 20), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film3));
    }

    @Test
    void testNotAddFilmWithDescriptionMoreThan200Characters() {
        final Film film1 = Film.of(0, "Film name1",
                "Too long description Too long description Too long description Too long description" +
                        "Too long description Too long description Too long description Too long description" +
                        "Too long description Too long description Too long description Too long description",
                LocalDate.of(1995, 5, 20), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        // Boundary value 200 characters
        final Film film2 = Film.of(0, "Film name2",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long description",
                LocalDate.of(1995, 5, 20), 120);
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value 199 characters
        final Film film3 = Film.of(0, "Film name3",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptio",
                LocalDate.of(1995, 5, 20), 120);
        assertDoesNotThrow(() -> filmController.addFilm(film3));

        // Boundary value 201 characters
        final Film film4 = Film.of(0, "Film name4",
                "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long descriptionToo long descriptionToo long description" +
                        "Too long descriptionToo long description ",
                LocalDate.of(1995, 5, 20), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));
    }

    @Test
    void testNotAddFilmWithDateOfReleaseBefore28121895() {
        final Film film1 = Film.of(0, "Film name1", "Film description",
                LocalDate.of(895, 12, 28), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0, "Film name2", "Film description",
                LocalDate.of(2020, 10, 20), 120);
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value date 1895-12-28
        final Film film3 = Film.of(0, "Film name3", "Film description",
                LocalDate.of(1895, 12, 28), 120);
        assertDoesNotThrow(() -> filmController.addFilm(film3));

        // Boundary value date 1895-12-27
        final Film film4 = Film.of(0, "Film name4", "Film description",
                LocalDate.of(1895, 12, 27), 120);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));

        // Boundary value date 1895-12-29
        final Film film5 = Film.of(0, "Film name5", "Film description",
                LocalDate.of(1895, 12, 29), 120);
        assertDoesNotThrow(() -> filmController.addFilm(film5));
    }

    @Test
    void testNotAddFilmWithZeroOrNegativeDuration() {
        final Film film1 = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), -100);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film1));

        final Film film2 = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 90);
        assertDoesNotThrow(() -> filmController.addFilm(film2));

        // Boundary value duration 0
        final Film film3 = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 0);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film3));

        // Boundary value duration -1
        final Film film4 = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), -1);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film4));

        // Boundary value duration 1
        final Film film5 = Film.of(0, "Film name", "Film description",
                LocalDate.of(1995, 5, 20), 1);
        assertDoesNotThrow(() -> filmController.addFilm(film5));
    }
}
