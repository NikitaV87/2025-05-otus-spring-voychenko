package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.CommentDto;
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
    public Optional<CommentDto> findById(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(commentMapper.toDto(comment.get()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(Long id) {
        return commentMapper.toDto(commentRepository.findByBookId(id));
    }

    @Transactional
    @Override
    public CommentDto insert(String text, Long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        Comment comment = Comment.builder().text(text).book(book).build();

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(Long commentId, String text) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(commentId)));

        comment.setText(text);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
