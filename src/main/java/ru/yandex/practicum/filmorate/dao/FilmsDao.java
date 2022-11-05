package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmsDao {
    Film create(Film film);
    Film update(Film film);
    void delete(Film film);
    List<Film> getAll();
    Film get(long filmId);
    List<Film> getPopular(int size);
}
