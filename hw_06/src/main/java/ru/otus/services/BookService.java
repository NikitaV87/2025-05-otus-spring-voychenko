package ru.otus.services;

import ru.otus.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book insert(String title, long authorId, Set<Long> genresIds);

    Book update(Long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(Long id);
}
