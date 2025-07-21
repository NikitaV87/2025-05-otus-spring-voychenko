package ru.otus.repositories;

import ru.otus.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAllByIds(Set<Long> ids);
}
