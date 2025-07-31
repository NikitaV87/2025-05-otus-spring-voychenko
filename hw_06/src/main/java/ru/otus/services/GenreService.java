package ru.otus.services;

import ru.otus.models.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();
}
