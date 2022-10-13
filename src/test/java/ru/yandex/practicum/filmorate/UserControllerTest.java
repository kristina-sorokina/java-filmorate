package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private final UserController userController;
    private Validator validator;

    @Autowired
    public UserControllerTest(UserController userController) {
        this.userController = userController;
    }

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void checkNullEmail() {
        User testUser = getTestUser();
        testUser.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(1, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("не должно равняться null", violation.getMessage());
            assertEquals("email", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkWrongFormatEmail() {
        User testUser = getTestUser();
        testUser.setEmail("wrong_email"); // http://www.ex-parrot.com/~pdw/Mail-RFC822-Address.html

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(1, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("должно иметь формат адреса электронной почты", violation.getMessage());
            assertEquals("email", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkNullLogin() {
        User testUser = getTestUser();
        testUser.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(2, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            if (violation.getMessageTemplate().equals("{javax.validation.constraints.NotNull.message}")) {
                assertEquals("не должно равняться null", violation.getMessage());
                assertEquals("login", violation.getPropertyPath().toString());
            }
            if (violation.getMessageTemplate().equals("{javax.validation.constraints.NotBlank.message}")) {
                assertEquals("не должно быть пустым", violation.getMessage());
                assertEquals("login", violation.getPropertyPath().toString());
            }
        }
    }

    @Test
    public void checkEmptyLogin() {
        User testUser = getTestUser();
        testUser.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(1, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("не должно быть пустым", violation.getMessage());
            assertEquals("login", violation.getPropertyPath().toString());
        }
    }


    @Test
    public void checkNullName() throws ValidationException {
        User testUser = getTestUser();
        testUser.setName(null);

        User responseUser = userController.createUser(testUser);
        assertEquals(testUser.getLogin(), responseUser.getName());
    }

    @Test
    public void checkEmptyName() throws ValidationException {
        User testUser = getTestUser();
        testUser.setName("");

        User responseUser = userController.createUser(testUser);
        assertEquals(testUser.getLogin(), responseUser.getName());
    }

    @Test
    public void checkPresentBirthday() {
        User testUser = getTestUser();
        testUser.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(1, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("должно содержать прошедшую дату", violation.getMessage());
            assertEquals("birthday", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkFutureBirthday() {
        User testUser = getTestUser();
        testUser.setBirthday(LocalDate.now().plusMonths(1));

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);
        assertEquals(1, violations.size());
        for (ConstraintViolation<User> violation : violations) {
            assertEquals("должно содержать прошедшую дату", violation.getMessage());
            assertEquals("birthday", violation.getPropertyPath().toString());
        }
    }

    private User getTestUser() {
        User testUser = new User(); // приемлемые значения
        testUser.setName("Имя 1");
        testUser.setBirthday(LocalDate.of(1999, Month.JUNE, 17));
        testUser.setEmail("i.e.pavlov@ya.ru");
        testUser.setLogin("oh_pavlov");
        return testUser;
    }
}
