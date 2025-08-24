package ru.otus.services;

import ru.otus.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(String id);

    List<Comment> findByBookId(String id);

    Comment insert(String text, String bookId);

    Comment update(String commentId, String text);

    void deleteById(String commentId);
}
