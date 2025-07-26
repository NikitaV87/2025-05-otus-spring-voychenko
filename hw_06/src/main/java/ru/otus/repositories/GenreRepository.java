package ru.otus.repositories;

import ru.otus.models.Genre;

import java.util.Set;

public interface GenreRepository {
    Set<Genre> findAll();

    Set<Genre> findAllByIds(Set<Long> ids);
}
