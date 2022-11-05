package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UsersDao {
    User create(User user);
    User update(User user);
    void delete(User user);
    List<User> getAll();
    User get(long userId);
}
