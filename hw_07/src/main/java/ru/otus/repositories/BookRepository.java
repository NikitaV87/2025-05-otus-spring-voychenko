package ru.otus.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(value = "book-author-genres-graph")
    @Override
    Optional<Book> findById(Long aLong);

    @EntityGraph(value = "book-author-graph")
    List<Book> findAll();
}
