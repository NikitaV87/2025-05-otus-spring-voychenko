package ru.otus.controller.rest;

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
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.services.BookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping("/api/book")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/api/book/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @DeleteMapping("/api/book/{id}")
    public void deleteBookById(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PostMapping("/api/book")
    public ResponseEntity<BookDto> postBook(@Valid @RequestBody BookCreateDto bookCreateDto) {
        BookDto bookDto = bookService.insert(bookCreateDto);

        return ResponseEntity.ok(bookDto);
    }

    @PatchMapping("/api/book")
    public ResponseEntity<BookDto> patchBook(@Valid @RequestBody BookUpdateDto bookUpdateDto) {
        BookDto bookDto = bookService.update(bookUpdateDto);

        return ResponseEntity.ok(bookDto);
    }
}
