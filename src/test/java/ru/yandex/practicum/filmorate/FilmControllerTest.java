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
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);

        Film updatedFilm = new Film();
        updatedFilm.setId(1);
        updatedFilm.setName("Film updated name");
        updatedFilm.setDescription("Film updated description");
        updatedFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        updatedFilm.setDuration(2);

        filmController.addFilm(film);
        filmController.updateFilm(updatedFilm);
        Film savedFilm = filmController.getFilms().get(0);

        assertEquals(1, filmController.getFilms().size(), "Number of films in not equal");
        assertEquals(updatedFilm, savedFilm, "Film is not equal");
    }

    @Test
    void updateWithUnknownId() throws ValidationException {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);
        filmController.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(222);
        updatedFilm.setName("Film updated name");
        updatedFilm.setDescription("Film updated description");
        updatedFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        updatedFilm.setDuration(2);

        assertThrows(ResourceNotFoundException.class, () -> filmController.updateFilm(updatedFilm), "Film with id " + film.getId() + " not found");
    }
}
