package ru.yandex.practicum.filmorate.dao.likes;


public interface LikesDao {
    void create(long filmId, long userId);
    void remove(long filmId, long userId);
}
