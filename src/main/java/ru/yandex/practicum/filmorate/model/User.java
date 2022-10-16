package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private final Set<Long> friends = new HashSet<>();
    private final Set<Long> likes = new HashSet<>();

    public void addFriend(long friendId) {
        friends.add(friendId);
    }

    public void addLike(long filmId) {
        likes.add(filmId);
    }

    public void removeLike(long filmId) {
        likes.remove(filmId);
    }

    public void removeFriend(long friendId) {
        friends.remove(friendId);
    }
}
