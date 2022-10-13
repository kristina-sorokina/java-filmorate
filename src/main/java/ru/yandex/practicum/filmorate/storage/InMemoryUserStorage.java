package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final IdGenerator idGenerator;

    @Autowired
    public InMemoryUserStorage(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public User create(User user) {
        user.setId(idGenerator.nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Attempt to update user with non existent id: {}", user.getId());
            throw new UserNotFoundException(user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {

    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(userId);
        }
        return users.get(userId);
    }
}
