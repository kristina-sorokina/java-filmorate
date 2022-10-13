package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {
    private final FilmController filmController;
    private Validator validator;

    @Autowired
    public FilmControllerTest(FilmController filmController) {
        this.filmController = filmController;
    }

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void checkNullFilmName() {
        Film testFilm = getTestFilm();
        testFilm.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(2, violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            if (violation.getMessageTemplate().equals("{javax.validation.constraints.NotNull.message}")) {
                assertEquals("не должно равняться null", violation.getMessage());
                assertEquals("name", violation.getPropertyPath().toString());
            }
            if (violation.getMessageTemplate().equals("{javax.validation.constraints.NotBlank.message}")) {
                assertEquals("не должно быть пустым", violation.getMessage());
                assertEquals("name", violation.getPropertyPath().toString());
            }
        }
    }

    @Test
    public void checkEmptyFilmName() {
        Film testFilm = getTestFilm();
        testFilm.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            assertEquals("не должно быть пустым", violation.getMessage());
            assertEquals("name", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void check200SymbolsFilmDescription() {
        Film testFilm = getTestFilm();
        String descriptionWithValidLength = getStringOf200Characters();
        testFilm.setDescription(descriptionWithValidLength);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(0, violations.size());
    }

    @Test
    public void checkMoreThan200SymbolsFilmDescription() {
        Film testFilm = getTestFilm();
        String descriptionWithInvalidLength = getStringOf201Characters();
        testFilm.setDescription(descriptionWithInvalidLength);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            assertEquals("размер должен находиться в диапазоне от 0 до 200", violation.getMessage());
            assertEquals("description", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkNegativeFilmDuration() {
        Film testFilm = getTestFilm();
        testFilm.setDurationMin(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            assertEquals("должно быть больше 0", violation.getMessage());
            assertEquals("durationMin", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkZeroFilmDuration() {
        Film testFilm = getTestFilm();
        testFilm.setDurationMin(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(1, violations.size());
        for (ConstraintViolation<Film> violation : violations) {
            assertEquals("должно быть больше 0", violation.getMessage());
            assertEquals("durationMin", violation.getPropertyPath().toString());
        }
    }

    @Test
    public void checkPositiveFilmDuration() {
        Film testFilm = getTestFilm();
        testFilm.setDurationMin(1);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);
        assertEquals(0, violations.size());
    }

    @Test
    public void checkEarlierFilmReleaseDate() {
        Film testFilm = getTestFilm();
        testFilm.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(testFilm);
        });
        assertEquals("Дата релиза не прошла валидацию!", exception.getMessage());
    }

    @Test
    public void checkNullFilm() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            filmController.createFilm(null);
        });
        assertEquals("Тело запроса пустое (должен быть объект Film)", exception.getMessage());
    }

    private Film getTestFilm() {
        Film testFilm = new Film(); // приемлемые значения
        testFilm.setName("Имя 1");
        testFilm.setDescription("Описание 1");
        testFilm.setReleaseDate(LocalDate.now());
        testFilm.setDurationMin(100);
        return testFilm;
    }

    private String getStringOf200Characters() {
        return "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789";
    }

    private String getStringOf201Characters() {
        return "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" +
                "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "!";
    }
}
