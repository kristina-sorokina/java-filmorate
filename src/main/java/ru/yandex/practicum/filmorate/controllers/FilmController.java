package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate FILM_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private int idGenerator = 0;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(++idGenerator);
        films.put(film.getId(), film);
        log.info("Film added: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException, ResourceNotFoundException {
        if (!films.containsKey(film.getId())) {
            log.error("Attempt to update movie with non-existent id: {}", film.getId());
            throw new ResourceNotFoundException("Film with id " + film.getId() + " not found");
        }
        validate(film);
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
    }

    private void validate(Film film) throws ValidationException {
        if (film == null) {
            log.error("Request body cannot be empty (Film object required)");
            throw new ValidationException("Request body cannot be empty");
        }

        if (film.getName().isBlank()) {
            log.error("Film name cannot be empty");
            throw new ValidationException("Film name cannot be empty");
        }

        if (film.getDescription().length() > 200) {
            log.error("Film description cannot be more then 200");
            throw new ValidationException("Film description cannot be more then 200");
        }

        if (film.getReleaseDate().isBefore(FILM_BIRTHDAY)) {
            log.error("Film release date cannot be earlier then 1895-12-28");
            throw new ValidationException("Film release date cannot be earlier then 1895-12-28");
        }

        if (film.getDuration() < 0) {
            log.error("Film duration cannot be negative");
            throw new ValidationException("Film duration cannot be negative");
        }
    }
}
