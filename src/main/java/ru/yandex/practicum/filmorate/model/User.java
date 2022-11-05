package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    @Email
    @NotNull
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @Past
    @NotNull
    private LocalDate birthday;
}
