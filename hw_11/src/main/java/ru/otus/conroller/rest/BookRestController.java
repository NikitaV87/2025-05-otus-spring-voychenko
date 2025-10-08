package ru.otus.conroller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.domain.Book;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.exception.NotFoundException;
import ru.otus.mapper.BookMapper;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.GenreRepository;

import static java.text.MessageFormat.format;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookRestController {
    private final BookRepository bookRepository;

    private final GenreRepository genreRepository;

    private final AuthorRepository authorRepository;

    private final BookMapper bookMapper;

    @GetMapping("/api/book")
    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAll().map(bookMapper::toDto);
    }

    @GetMapping("/api/book/{id}")
    public Mono<BookDto> getBookById(@PathVariable String id) {
        return bookRepository.findById(id)
            .switchIfEmpty(Mono.error(new NotFoundException("Book not found")))
            .map(bookMapper::toDto);
    }

    @DeleteMapping("/api/book/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable String id) {
        bookRepository.deleteById(id);
    }

    @PostMapping("/api/book")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<BookDto> postBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        return Mono.just(bookCreateDto)
            .map(bookMapper::fromCreateDto)
            .flatMap(b -> checkGenreExists(Mono.just(b)))
            .flatMap(b -> checkAuthorExists(Mono.just(b)))
            .flatMap(bookRepository::save)
            .map(bookMapper::toDto);
    }

    @PatchMapping("/api/book")
    public Mono<BookDto> patchBook(@Valid @RequestBody BookUpdateDto bookDto) {
        return Mono.just(bookDto)
            .map(bookMapper::fromUpdDto)
            .flatMap(book -> checkGenreExists(Mono.just(book)))
            .flatMap(book -> checkAuthorExists(Mono.just(book)))
            .flatMap(book -> bookRepository.existsById(book.getId()).flatMap(isExists -> {
                  if (!isExists) {
                      return Mono.error(new NotFoundException(format("Book {0} not found", book.getId())));
                  } else {
                      return Mono.just(true);
                  }
                }).thenReturn(book))
            .flatMap(bookRepository::save)
            .map(bookMapper::toDto);
    }

    private Mono<Book> checkGenreExists(Mono<Book> bookMono) {
        return bookMono.flatMap(book -> Flux.fromIterable(book.getGenres())
            .flatMap(genre -> genreRepository.existsById(genre.getId()).flatMap(isExists -> {
                if (!isExists) {
                    return  Mono.error(new NotFoundException(format("Genre not found: {0}", genre.getId())));
                } else {
                    return Mono.just(true);
                }
            })).next().thenReturn(book));
    }

    private Mono<Book> checkAuthorExists(Mono<Book> bookMono) {
        return bookMono.flatMap(book -> authorRepository.existsById(book.getAuthor().getId()).flatMap(isExists -> {
            if (!isExists) {
                return Mono.error(
                        new NotFoundException(format("Author not found: {0}", book.getAuthor().getId())));
            } else {
                return Mono.just(true);
            }
        }).thenReturn(book));
    }
}
