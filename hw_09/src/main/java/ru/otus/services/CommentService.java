package ru.otus.services;

import ru.otus.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(Long id);

    List<CommentDto> findByBookId(Long id);

    CommentDto insert(String text, Long bookId);

    CommentDto update(Long commentId, String text);

    void deleteById(Long commentId);
}
