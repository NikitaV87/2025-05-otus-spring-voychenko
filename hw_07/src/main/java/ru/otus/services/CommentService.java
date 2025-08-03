package ru.otus.services;

import ru.otus.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(Long id);

    List<Comment> findByBookIdWithBook(Long id);

    Comment insert(String text, Long bookId);

    Comment update(Long commentId, String text);

    void deleteById(Long commentId);
}
