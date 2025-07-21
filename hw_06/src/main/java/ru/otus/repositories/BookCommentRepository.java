package ru.otus.repositories;

import ru.otus.models.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentRepository {
    Optional<BookComment> findById(Long id);

    List<BookComment> findByBookId(Long id);

    BookComment save(BookComment bookComment);

    void delete(BookComment bookComment);
}
