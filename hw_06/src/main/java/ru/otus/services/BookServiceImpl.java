package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.BookComment;
import ru.otus.models.Genre;
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

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(long id) {
        return bookRepository.findByIdWithFetchComments(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        List<Book> books = bookRepository.findAllWithFetchComments();

        return books;
    }

    @Transactional
    @Override
    public Book insert(String title, long authorId, Set<Long> genresIds, List<String> bookComments) {
        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Book book = Book.builder().id(null).title(title).author(author).genres(genres).build();
        setCommentToBook(book, bookComments);

        return bookRepository.save(book);
    }

    @Transactional
    @Override
    public Book update(Long id, String title, long authorId, Set<Long> genresIds, List<String> commentsText) {

        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(authorId));
        }

        book.get().setTitle(title);
        book.get().setAuthor(author);
        book.get().setGenres(genres);
        setCommentToBook(book.get(), commentsText);
        bookRepository.save(book.get());

        return book.get();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
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

    private Set<Genre> getGenresWithValidate(Set<Long> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var genres = genreRepository.findAllByIds(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }

        return genres;
    }

    private Author getAuthorWithValidate(Long authorId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        return author;
    }
}
