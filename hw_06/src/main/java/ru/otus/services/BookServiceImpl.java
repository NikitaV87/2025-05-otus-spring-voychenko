package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.models.Book;
import ru.otus.models.BookComment;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.GenreRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    @Override
    public Book insert(String title, long authorId, Set<Long> genresIds, List<String> bookComments) {
        return save(null, title, authorId, genresIds, bookComments);
    }

    @Transactional
    @Override
    public Book update(Long id, String title, long authorId, Set<Long> genresIds, List<String> commentsText) {
        return save(id, title, authorId, genresIds, commentsText);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private Book save(Long id, String title, long authorId, Set<Long> genresIds, List<String> commentsText) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        Optional<Book> book;
        if (id == null) {
            book = Optional.of(Book.builder().id(id).title(title).author(author).genres(genres).build());
        } else {
            book = bookRepository.findById(id);
            if (book.isEmpty()) {
                throw new EntityNotFoundException("Book id %d not found".formatted(id));
            }
            book.get().setTitle(title);
            book.get().setAuthor(author);
            book.get().setGenres(genres);
        }
        setCommentToBook(book.get(), commentsText);
        return bookRepository.save(book.get());
    }

    void setCommentToBook(Book book, List<String> commentsText) {
        List<BookComment> comments = new ArrayList<>();

        if (commentsText != null && commentsText.size() > 0) {
            for (String commentText : commentsText) {
                comments.add(BookComment.builder().text(commentText).book(book).build());
            }
        }

        book.setComments(comments);
    }
}
