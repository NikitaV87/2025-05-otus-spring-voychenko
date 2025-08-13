package ru.otus.services;

import ru.otus.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
}
