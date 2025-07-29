package ru.otus.repositories;

import ru.otus.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findById(Long id);

    List<Comment> findByBookId(Long id);

    Comment save(Comment comment);

    void delete(Comment comment);
}
