package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("/films (POST): {}", film);
        validateFilm(film);
        return filmService.create(film);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<Film> getFilms() {
        log.info("/films (GET)");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Film getFilm(@PathVariable("id") long id) {
        log.info("/films/{} (GET)", id);
        return filmService.get(id);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("/films (PUT): {}", film);
        validateFilm(film);
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void like(@PathVariable("id") long id,
                     @PathVariable("userId") long userId) {
        log.info("/films/{}/like/{} (PUT)", id, userId);
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable("id") long id,
                       @PathVariable("userId") long userId) {
        log.info("/films/{}/like/{} (DELETE)", id, userId);
        filmService.unlike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("/films/popular?count={} (GET)", count);
        return filmService.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            log.error("Тело запроса пустое (должен быть объект Film)");
            throw new ValidationException("Тело запроса пустое (должен быть объект Film)");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза не прошла валидацию: {}", film);
            throw new ValidationException("Дата релиза не прошла валидацию!");
        }
    }
}
