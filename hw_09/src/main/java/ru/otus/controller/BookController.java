package ru.otus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.services.AuthorService;
import ru.otus.services.BookService;
import ru.otus.services.GenreService;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/book")
    public String getAllBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "book/book";
    }

    @GetMapping("book/new")
    public String getCreateNewBook(Model model) {
        model.addAttribute("book", new BookCreateDto());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "book/formCreate";
    }

    @GetMapping("book/{id}")
    public String getUpdateBook(@PathVariable Long id, Model model) {
        BookDto book = bookService.findById(id);

        model.addAttribute("book", BookUpdateDto.builder()
                .id(book.getId())
                .authorId(book.getAuthor().getId())
                .title(book.getTitle())
                .genreIds(book.getGenresId()).build());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "book/formUpd";
    }

    @PostMapping("/book/new")
    public String postCreateNewBook(@Valid @ModelAttribute("book") BookCreateDto bookCreateDto,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "book/formCreate";
        }

        bookService.insert(bookCreateDto);

        return "redirect:/book";
    }

    @PostMapping("book/{id}")
    public String postUpdateBook(@Valid @ModelAttribute("book") BookUpdateDto bookUpdateDto,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "book/formUpd";
        }

        bookService.update(bookUpdateDto);

        return "redirect:/book";
    }

    @PostMapping("book/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/book";
    }
}
