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
import ru.otus.dto.BookDto;
import ru.otus.dto.request.RequestCreateAndUpdateBook;
import ru.otus.dto.response.ResponseCreateAndUpdateBook;
import ru.otus.mapper.response.ResponseCreateOrUpdateBookMapper;
import ru.otus.services.AuthorService;
import ru.otus.services.BookService;
import ru.otus.services.GenreService;

import java.util.Optional;

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
        model.addAttribute("book", new ResponseCreateAndUpdateBook());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "book/form";
    }

    @GetMapping("book/{id}")
    public String getUpdateBook(@PathVariable Long id, Model model) {
        Optional<BookDto> book = bookService.findById(id);

        if (book.isEmpty()) {
            model.addAttribute("message", "book not find!");
            return "error/message";
        }
        model.addAttribute("book", ResponseCreateOrUpdateBookMapper.toResponse(book.get()));
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());

        return "book/form";
    }

    @PostMapping("/book/new")
    public String postCreateNewBook(@Valid @ModelAttribute("book") RequestCreateAndUpdateBook request,
                               BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "book/form";
        }

        bookService.insert(request.getTitle(), request.getAuthor(), request.getGenres());

        return "redirect:/book";
    }

    @PostMapping("book/{id}")
    public String postUpdateBook(@PathVariable Long id,
                       @Valid @ModelAttribute("book") RequestCreateAndUpdateBook request,
                       BindingResult bindingResult,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authorService.findAll());
            model.addAttribute("genres", genreService.findAll());
            return "book/form";
        }

        bookService.update(id, request.getTitle(), request.getAuthor(), request.getGenres());

        return "redirect:/book";
    }

    @PostMapping("book/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/book";
    }
}
