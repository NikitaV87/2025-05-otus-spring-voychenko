package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.mapper.CommentMapper;
import ru.otus.models.Comment;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public CommentDto findById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            return null;
        }

        return commentMapper.toDto(comment.get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(Long id) {
        return commentMapper.toDto(commentRepository.findByBookId(id));
    }

    @Transactional
    @Override
    public CommentDto insert(CommentCreateDto commentCreateDto) {
        var book = bookRepository.findById(commentCreateDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found"
                        .formatted(commentCreateDto.getBookId())));

        Comment comment = Comment.builder().text(commentCreateDto.getText()).book(book).build();

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(CommentUpdateDto commentUpdateDto) {
        var comment = commentRepository.findById(commentUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found"
                        .formatted(commentUpdateDto.getId())));

        comment.setText(commentUpdateDto.getText());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
