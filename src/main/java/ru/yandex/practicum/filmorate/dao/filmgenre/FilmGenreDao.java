package ru.yandex.practicum.filmorate.dao.filmgenre;

import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.util.List;

public interface FilmGenreDao {
    void create(FilmGenres filmGenres);
    void remove(FilmGenres filmGenres);
    List<FilmGenres> getByFilmId(long filmId);
}
