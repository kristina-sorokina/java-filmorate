package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    void update() throws ValidationException, ResourceNotFoundException {
        Film film = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        Film updatedFilm = Film.builder()
                .id(1)
                .name("Film updated name")
                .description("Film updated description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(2)
                .build();

        filmController.addFilm(film);
        filmController.updateFilm(updatedFilm);
        Film savedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(), "Number of films in not equal");
        assertEquals(updatedFilm, savedFilm, "Film is not equal");
    }

    @Test
    void updateWithUnknownId() throws ValidationException {
        Film film = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        filmController.addFilm(film);
        Film updatedFilm = Film.builder()
                .id(222)
                .name("Film updated name")
                .description("Film updated description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> filmController.updateFilm(updatedFilm), "Film with id " + film.getId() + " not found");
    }
}
