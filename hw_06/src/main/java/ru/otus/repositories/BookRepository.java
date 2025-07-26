package ru.otus.repositories;

import ru.otus.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(long id);

    Optional<Book> findByIdWithFetchComments(long id);

    List<Book> findAll();

    List<Book> findAllWithFetchComments();

    Book save(Book book);

    void deleteById(long id);
}
