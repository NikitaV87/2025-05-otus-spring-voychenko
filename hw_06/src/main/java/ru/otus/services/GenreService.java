package ru.otus.services;

import ru.otus.models.Genre;

import java.util.Set;

public interface GenreService {
    Set<Genre> findAll();
}
