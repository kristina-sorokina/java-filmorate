package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.OK)
    public User createUser(@Valid @RequestBody User user) {
        log.info("/users (POST): {}", user);
        validateUser(user);
        return userService.create(user);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<User> getUsers() {
        log.info("/users (GET)");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public User getUser(@PathVariable("id") long id) {
        log.info("/users/{} (GET)", id);
        return userService.get(id);
    }

    @PutMapping
    @ResponseStatus(value = HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        log.info("/users (PUT): {}", user);
        validateUser(user);
        return userService.update(user);
    }
    @GetMapping("/{id}/friends")
    @ResponseStatus(value = HttpStatus.OK)
    public List<User> getFriends(@PathVariable("id") long id) {
        log.info("/users/{}/friends (GET)", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<User> getFriends(@PathVariable("id") long id,
                                 @PathVariable("otherId") long otherId) {
        log.info("/users/{}/friends/common/{} (GET)", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void makeFriends(@PathVariable("id") long id,
                            @PathVariable("friendId") long friendId) {
        log.info("/users/{}/friends/{} (PUT)", id, friendId);
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void stopBeingFriends(@PathVariable("id") long id,
                                 @PathVariable("friendId") long friendId) {
        log.info("/users/{}/friends/{} (DELETE)", id, friendId);
        userService.stopBeingFriends(id, friendId);
    }


    private void validateUser(User user) {
        if (user == null) {
            log.error("Request body cannot be empty (User object required)");
            throw new ValidationException("Request body cannot be empty");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Login cannot be empty or cannot contain whitespaces: {}", user);
            throw new ValidationException("Login cannot be empty or cannot contain whitespaces");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Empty user name replaced with login: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
