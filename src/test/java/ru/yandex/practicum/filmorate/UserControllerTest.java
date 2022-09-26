package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    void update() throws ValidationException, ResourceNotFoundException {
        User user = User.builder()
                .login("Userlogin")
                .name("User name")
                .email("user@gmail.com")
                .birthday(LocalDate.of(1986, 6, 18))
                .build();
        userController.addUser(user);
        User updatedUser = User.builder()
                .id(1)
                .login("Userlogin")
                .name("Updated name")
                .email("user@gmail.com")
                .birthday(LocalDate.of(1996, 6, 18))
                .build();
        userController.updateUser(updatedUser);
        User savedUser = userController.getUsers().get(0);
        assertEquals(1, userController.getUsers().size(), "Number of users in not equal");
        assertEquals(updatedUser, savedUser, "User is not equal");
    }

    @Test
    void updateWithUnknownId() throws ValidationException {
        User user = User.builder()
                .login("Userlogin")
                .name("User name")
                .email("user@gmail.com")
                .birthday(LocalDate.of(1996, 6, 18))
                .build();
        userController.addUser(user);
        User updatedUser = User.builder()
                .id(222)
                .login("Userlogin")
                .name("Updated name")
                .email("user@gmail.com")
                .birthday(LocalDate.of(2022, 1, 1))
                .build();
        assertThrows(ResourceNotFoundException.class, () -> userController.updateUser(updatedUser), "User with id " + user.getId() + " not found");
    }

}
