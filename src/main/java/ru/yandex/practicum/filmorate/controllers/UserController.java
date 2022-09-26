package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;


    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws ValidationException {
        validate(user);
        user.setId(++idGenerator);
        if(user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User added: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, ResourceNotFoundException {
        if (!users.containsKey(user.getId())) {
            log.error("Attempt to update user with non-existent id: {}", user.getId());
            throw new ResourceNotFoundException("User with id " + user.getId() + " not found");
        }
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validate(User user) throws ValidationException {
        if (user == null) {
            log.error("Request body cannot be empty (User object required)");
            throw new ValidationException("Request body cannot be empty");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Email name cannot be empty and must contain @");
            throw new ValidationException("Email name cannot be empty and must contain @");
        }

        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Login cannot be empty or cannot contain whitespaces");
            throw new ValidationException("Login cannot be empty or cannot contain whitespaces");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("User birthday cannot be in the future");
            throw new ValidationException("User birthday cannot be in the future");
        }
    }
}
