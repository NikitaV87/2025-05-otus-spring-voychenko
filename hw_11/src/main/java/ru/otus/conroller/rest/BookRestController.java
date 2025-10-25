package ru.otus.conroller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.exception.NotFoundException;
import ru.otus.mapper.BookMapper;
import ru.otus.repositories.BookRepository;
import ru.otus.service.BookTemplateOperations;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BookRestController {
    private final BookTemplateOperations bookTemplateOperations;

    private final BookRepository bookRepository;


    private final BookMapper bookMapper;

    @GetMapping("/api/book")
    public Flux<BookDto> getAllBooks() {
        return bookTemplateOperations.findAll();
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
        log.info("id:" + id);
        bookRepository.deleteById(id);
    }

    @PostMapping("/api/book")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<BookDto> postBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        return Mono.just(bookCreateDto)
            .map(bookMapper::fromCreateDto)
            .flatMap(bookRepository::save)
            .map(bookMapper::toDto);
    }

    @PutMapping("/api/book")
    public Mono<BookDto> putBook(@Valid @RequestBody BookUpdateDto bookDto) {
        return Mono.just(bookDto)
            .map(bookMapper::fromUpdDto)
            .flatMap(bookRepository::save)
            .map(bookMapper::toDto);
    }
}
