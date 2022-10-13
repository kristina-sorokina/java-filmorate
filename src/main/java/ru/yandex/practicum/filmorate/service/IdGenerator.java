package ru.yandex.practicum.filmorate.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdGenerator {
    private long currentId;

    public long nextId() {
        return ++currentId;
    }
}
