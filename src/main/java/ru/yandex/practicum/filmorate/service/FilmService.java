package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmsDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmsDao filmsDao;
    private final LikesDao likesDao;

    @Autowired
    public FilmService(@Qualifier("filmsDaoImpl") FilmsDao filmsDao, LikesDao likesDao) {
        this.filmsDao = filmsDao;
        this.likesDao = likesDao;
    }

    public Film create(Film film) {
        log.info("Создание фильма: {}", film);
        return filmsDao.create(film);
    }

    public Film update(Film film) {
        log.info("Обновление фильма: {}", film);
        return filmsDao.update(film);
    }

    public List<Film> getAll() {
        log.info("Получение всех фильмов");
        return filmsDao.getAll();
    }

    public Film get(long id) {
        log.info("Получение фильма {}", id);
        return filmsDao.get(id);
    }

    public void like(long filmId, long userId) {
        likesDao.create(filmId, userId);
    }

    public void unlike(long filmId, long userId) {
        likesDao.remove(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> popularFilms = filmsDao.getPopular(count);
        if (popularFilms.isEmpty()) {
            return filmsDao.getAll().stream()
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            return popularFilms;
        }
    }
}
