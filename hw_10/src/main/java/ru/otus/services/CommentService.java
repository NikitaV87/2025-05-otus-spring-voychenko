package ru.otus.services;

import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;

import java.util.List;

public interface CommentService {
    CommentDto findById(Long id);

    List<CommentDto> findByBookId(Long id);

    CommentDto insert(CommentCreateDto commentCreateDto);

    CommentDto update(CommentUpdateDto commentUpdateDto);

    void deleteById(Long commentId);
}
