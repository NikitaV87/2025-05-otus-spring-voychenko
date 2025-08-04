package ru.otus.repositories;

import ru.otus.models.Genre;

import java.util.List;

public interface GenreRepository {
    List<Genre> findAll();

    List<Genre> findAllByIds(List<Long> ids);
}
