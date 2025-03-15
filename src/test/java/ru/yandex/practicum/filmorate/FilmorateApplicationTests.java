package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {
    @Autowired
    private TestRestTemplate template;

    @Test
    void contextLoads() {
    }

    // Film tests

    @Test
    void shouldAddNewCorrectFilm() {
        Film film = Film.of(0,
                "Film name",
                "Film description",
                LocalDate.of(1995, 5, 20),
                120);

        ResponseEntity<Film> entity = template.postForEntity("/films", film, Film.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        Film createdFilm = entity.getBody();
        assertEquals(1, createdFilm.getId());
        assertEquals("Film name", createdFilm.getName());
        assertEquals("Film description", createdFilm.getDescription());
        assertEquals(LocalDate.of(1995, 5, 20), createdFilm.getReleaseDate());
        assertEquals(120, createdFilm.getDuration());

        assertEquals(1, template.getForObject("/films", List.class).size());
    }

    @Test
    void shouldUpdateCorrectFilmAndGetAllFilms() {
        Film film = Film.of(1,
                "Film name111",
                "Film description111",
                LocalDate.of(2005, 3, 22),
                100);

        template.put("/films", film);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<Film>> entity = template.exchange("/films", HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Film>>() {
                });

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());

        List<Film> films = entity.getBody();
        assertEquals(1, films.get(0).getId());
        assertEquals("Film name111", films.get(0).getName());
        assertEquals("Film description111", films.get(0).getDescription());
        assertEquals(LocalDate.of(2005, 3, 22), films.get(0).getReleaseDate());
        assertEquals(100, films.get(0).getDuration());
    }

    @Test
    void testEmptyBodyRequestForFilmsPostMethod() {
        ResponseEntity<String> entity1 = template.postForEntity("/films", "", String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity1.getStatusCode());

        entity1 = template.postForEntity("/films", "{}", String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity1.getStatusCode());

        ResponseEntity<Film> entity2 = template.postForEntity("/films", "", Film.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity2.getStatusCode());

        entity2 = template.postForEntity("/films", "{}", Film.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity2.getStatusCode());
    }

    // User tests

    @Test
    void shouldAddNewCorrectUser() {
        User user = User.of(0,
                "User name",
                "123@mail.com",
                "qwerty12345",
                LocalDate.of(1990, 12, 12));

        ResponseEntity<User> entity = template.postForEntity("/users", user, User.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        User createdUser = entity.getBody();
        assertEquals(1, createdUser.getId());
        assertEquals("User name", createdUser.getName());
        assertEquals("123@mail.com", createdUser.getEmail());
        assertEquals("qwerty12345", createdUser.getLogin());
        assertEquals(LocalDate.of(1990, 12, 12), createdUser.getBirthday());

        assertEquals(1, template.getForObject("/users", List.class).size());
    }

    @Test
    void shouldUpdateCorrectUserAndGetAllUsers() {
        User user = User.of(1,
                "User name111",
                "321@mail.ca",
                "qwe123",
                LocalDate.of(2000, 2, 4));

        template.put("/users", user);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<User>> entity = template.exchange("/users", HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<User>>() {
                });

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());

        List<User> users = entity.getBody();
        assertEquals(1, users.get(0).getId());
        assertEquals("User name111", users.get(0).getName());
        assertEquals("321@mail.ca", users.get(0).getEmail());
        assertEquals("qwe123", users.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 2, 4), users.get(0).getBirthday());
    }

    @Test
    void testEmptyBodyRequestForUsersPostMethod() {
        ResponseEntity<String> entity1 = template.postForEntity("/users", "", String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity1.getStatusCode());

        entity1 = template.postForEntity("/users", "{}", String.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity1.getStatusCode());

        ResponseEntity<User> entity2 = template.postForEntity("/users", "", User.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity2.getStatusCode());

        entity2 = template.postForEntity("/users", "{}", User.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity2.getStatusCode());
    }
}
