package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final IdGenerator idGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Film create(Film film) {
        film.setId(idGenerator.nextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Attempt to update film with non existent id: {}", film.getId());
            throw new FilmNotFoundException(film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Film film) {

    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(long filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(filmId);
        }
        return films.get(filmId);
    }
}
