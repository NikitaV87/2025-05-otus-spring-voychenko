package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.BookDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.mapper.BookMapper;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<BookDto> findById(long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(bookMapper.toDto(book.get()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();

        books.forEach(b -> Hibernate.initialize(b.getGenres()));

        return bookMapper.toDto(books);
    }

    @Transactional
    @Override
    public BookDto insert(String title, long authorId, List<Long> genresIds) {
        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Book book = Book.builder().id(null).title(title).author(author).genres(genres).build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public BookDto update(Long id, String title, long authorId, List<Long> genresIds) {

        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Optional<Book> findBook = bookRepository.findById(id);

        Book book = findBook
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(authorId)));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);
        book = bookRepository.save(book);

        return bookMapper.toDto(book);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private List<Genre> getGenresWithValidate(List<Long> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var genres = genreRepository.findAllByIdIn(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }

        return genres;
    }

    private Author getAuthorWithValidate(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
    }
}
