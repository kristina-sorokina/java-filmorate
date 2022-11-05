package ru.yandex.practicum.filmorate.dao.films;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenres;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dao.filmgenre.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.genres.GenresDao;
import ru.yandex.practicum.filmorate.dao.mpa.MpaDao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmsDaoImpl implements FilmsDao {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final FilmGenreDao filmGenreDao;
    private final GenresDao genresDao;

    @Autowired
    public FilmsDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao,
                        FilmGenreDao filmGenreDao, GenresDao genresDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.filmGenreDao = filmGenreDao;
        this.genresDao = genresDao;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO films " +
                "(name, description, release_date, duration, mpa_id, rate)\n" +
                "VALUES (?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement ps =
                    connection.prepareStatement(sqlQuery, new String[] {"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            ps.setInt(6, film.getRate());
            return ps;
        }, keyHolder);
        log.info("genereted key: {}", keyHolder.getKey());
        film.setId(keyHolder.getKey().longValue());
        addGenresForFilm(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, " +
                "release_date = ?, mpa_id = ?, rate = ?, duration = ?" +
                "WHERE id = ?";
        int amountOfUpdated = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getMpa().getId(), film.getRate(),
                film.getDuration(), film.getId());
        if (amountOfUpdated != 1) {
            throw new FilmNotFoundException();
        }
        removeGenresForFilm(film.getId());
        addGenresForFilm(film.getId(), film.getGenres());
        return get(film.getId());
    }

    @Override
    public void delete(Film film) {

    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films;";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowId) -> buildFilm(resultSet));
    }

    @Override
    public Film get(long filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?;";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery,
                    (resultSet, rowId) -> buildFilm(resultSet), filmId);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new FilmNotFoundException();
        }
        return film;
    }

    @Override
    public List<Film> getPopular(int size) {
        String sqlQuery = "SELECT f.id \n" +
                "FROM films AS f \n" +
                "LEFT JOIN likes AS l ON f.id = l.film_id \n" +
                "GROUP BY f.name \n" +
                "ORDER BY \n" +
                "    CASE WHEN l.film_id IS NULL THEN 1 ELSE 0 END, \n" +
                "    COUNT(*) DESC \n" +
                "LIMIT ?;";
        List<Integer> popularFilmIds = jdbcTemplate.queryForList(sqlQuery, Integer.class, size);
        return popularFilmIds.stream()
                .map(this::get)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public Film buildFilm(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        Set<Genre> genres = filmGenreDao.getByFilmId(id).stream()
                .map(filmGenres -> genresDao.get(filmGenres.getGenreId()))
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        log.info("genres: {}", genres);
        return new Film(id,
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                genres,
                mpaDao.get(resultSet.getLong("mpa_id")),
                resultSet.getInt("rate"));
    }

    private void addGenresForFilm(Long id, Set<Genre> genres) {
        if (genres == null) {
            return;
        }
        genres.stream()
                .map(genre -> new FilmGenres(id, genre.getId()))
                .forEach(filmGenreDao::create);
    }

    private void removeGenresForFilm(Long id) {
        List<FilmGenres> filmGenres = filmGenreDao.getByFilmId(id);
        filmGenres.forEach(filmGenreDao::remove);
    }
}
