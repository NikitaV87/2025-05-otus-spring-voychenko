package ru.otus.services;

import ru.otus.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book insert(String title, long authorId, List<Long> genresIds);

    Book update(Long id, String title, long authorId, List<Long> genresIds);

    void deleteById(Long id);
}
