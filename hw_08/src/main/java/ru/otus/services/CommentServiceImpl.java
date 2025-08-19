package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.exceptions.EntityNotFoundException;
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

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findById(String id) {

        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(String id) {
        return commentRepository.findByBookId(id);
    }

    @Transactional
    @Override
    public Comment insert(String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));

        Comment comment = Comment.builder().text(text).book(book).build();

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public Comment update(String commentId, String text) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %sÏ not found".formatted(commentId)));

        comment.setText(text);

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteById(String commentId) {
        commentRepository.deleteById(commentId);
    }
}
