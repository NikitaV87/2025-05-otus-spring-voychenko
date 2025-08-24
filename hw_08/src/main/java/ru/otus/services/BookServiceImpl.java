package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.BookDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.GenreRepository;
import ru.otus.utils.MappingBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final MappingBook mappingBook;

    @Override
    public Optional<BookDto> findById(String id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            return Optional.empty();
        }


        return Optional.of(mappingBook.mapToBookDto(book.get()));
    }

    @Override
    public List<BookDto> findAll() {
        List<BookDto> bookDTOs = new ArrayList<>();
        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            bookDTOs.add(mappingBook.mapToBookDto(book));
        }

        return bookDTOs;
    }

    @Transactional
    @Override
    public BookDto insert(String title, String authorId, List<String> genresIds) {
        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Book book = Book.builder().id(null).title(title).author(author).genres(genres).build();

        Book bookSave = bookRepository.save(book);
        return MappingBook.mapToBookDtoStatic(book);
    }

    @Transactional
    @Override
    public BookDto update(String id, String title, String authorId, List<String> genresIds) {

        var genres = getGenresWithValidate(genresIds);
        var author = getAuthorWithValidate(authorId);

        Optional<Book> findBook = bookRepository.findById(id);

        Book book = findBook
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(authorId)));

        book.setTitle(title);
        book.setAuthor(author);
        book.setGenres(genres);
        book = bookRepository.save(book);

        return MappingBook.mapToBookDtoStatic(book);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    private List<Genre> getGenresWithValidate(List<String> genreIds) {
        if (isEmpty(genreIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var genres = genreRepository.findAllByIdIn(genreIds);
        if (isEmpty(genres) || genreIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genreIds));
        }

        return genres;
    }

    private Author getAuthorWithValidate(String authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
    }
}
