package ru.otus.repositories;

import ru.otus.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository {
    List<Author> findAll();

    Optional<Author> findById(Long id);
}
