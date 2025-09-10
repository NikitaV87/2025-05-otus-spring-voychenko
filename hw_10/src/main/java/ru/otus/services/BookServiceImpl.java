package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
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
    public BookDto findById(long id) {
        Optional<Book> book = bookRepository.findById(id);

        return bookMapper.toDto(book.orElseThrow(() -> new EntityNotFoundException("Book not find!")));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();

        return bookMapper.toDto(books);
    }

    @Transactional
    @Override
    public BookDto insert(BookCreateDto bookCreateDto) {
        var genres = getGenresWithValidate(bookCreateDto.getGenreIds());
        var author = getAuthorWithValidate(bookCreateDto.getAuthorId());

        Book book = Book.builder().id(null).title(bookCreateDto.getTitle()).author(author).genres(genres).build();

        return bookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    @Override
    public BookDto update(BookUpdateDto bookUpdateDto) {

        var genres = getGenresWithValidate(bookUpdateDto.getGenreIds());
        var author = getAuthorWithValidate(bookUpdateDto.getAuthorId());

        Optional<Book> findBook = bookRepository.findById(bookUpdateDto.getId());

        Book book = findBook
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(author.getId())));

        book.setTitle(bookUpdateDto.getTitle());
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
