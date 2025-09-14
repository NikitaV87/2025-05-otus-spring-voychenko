package ru.otus.conroller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dto.BookDto;
import ru.otus.exception.NotFoundException;
import ru.otus.mapper.BookMapper;
import ru.otus.repositories.BookRepository;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookRepository bookRepository;

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
    public void deleteBookById(@PathVariable String id) {
        bookRepository.deleteById(id);
    }

    @PostMapping("/api/book")
    public Mono<ResponseEntity<BookDto>> postBook(@Valid @RequestBody BookDto bookDto) {
        return bookRepository.save(bookMapper.fromDto(bookDto))
                .map(book -> ResponseEntity.ok(bookMapper.toDto(book)));
    }

    @PatchMapping("/api/book")
    public Mono<ResponseEntity<BookDto>> patchBook(@Valid @RequestBody BookDto bookDto) {
        return bookRepository.save(bookMapper.fromDto(bookDto))
                .map(book -> ResponseEntity.ok(bookMapper.toDto(book)));
    }
}
