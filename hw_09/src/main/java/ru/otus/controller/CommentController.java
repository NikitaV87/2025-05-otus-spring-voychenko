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
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.services.BookService;
import ru.otus.services.CommentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final BookService bookService;

    private final CommentService commentService;

    @GetMapping("/comment/book")
    public String getAllBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "comment/book";
    }

    @GetMapping("/comment/book/{bookId}")
    public String getCommentsByBook(@PathVariable Long bookId, Model model) {
        List<CommentDto> comments = commentService.findByBookId(bookId);

        model.addAttribute("comments", comments);
        model.addAttribute("bookId", bookId);

        return "comment/comments";
    }

    @GetMapping("/comment/{id}")
    public String getUpdateComment(@PathVariable Long id, Model model) {
        CommentDto comment = commentService.findById(id);

        if (comment == null) {
            throw new EntityNotFoundException("Comment not found");
        }

        model.addAttribute("comment", CommentUpdateDto.builder()
                .bookId(comment.getBook().getId())
                .id(comment.getId())
                .text(comment.getText()).build());
        model.addAttribute("books", List.of(comment.getBook()));

        return "comment/formUpd";
    }

    @PostMapping("/comment/{id}")
    public String postUpdateComment(@Valid @ModelAttribute("comment") CommentUpdateDto commentUpdateDto,
                                 BindingResult bindingResult,
                                 Model model) {
        CommentDto comment = commentService.findById(commentUpdateDto.getId());

        if (comment == null) {
            throw new EntityNotFoundException("Comment not found");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("books", List.of(comment.getBook()));
            return "comment/formUpd";
        }

        commentService.update(commentUpdateDto);

        return "redirect:/comment/book/%d".formatted(commentUpdateDto.getBookId());
    }

    @GetMapping("/comment/new/book/{bookId}")
    public String getNewComment(@PathVariable Long bookId, Model model) {

        BookDto book = bookService.findById(bookId);

        if (book == null) {
            throw new EntityNotFoundException("Book not find!");
        }

        model.addAttribute("comment", CommentCreateDto.builder().bookId(book.getId()).build());
        model.addAttribute("books", List.of(book));

        return "comment/formCreate";
    }

    @PostMapping("/comment/new/book/{bookId}")
    public String postNewComment(@Valid @ModelAttribute("comment") CommentCreateDto commentCreateDto,
                                 BindingResult bindingResult,
                                 Model model) {
        BookDto book = bookService.findById(commentCreateDto.getBookId());

        if (book == null) {
            throw new EntityNotFoundException("Book not find!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("books", List.of(book));
            return "comment/formCreate";
        }

        commentService.insert(commentCreateDto);

        return "redirect:/comment/book/%d".formatted(commentCreateDto.getBookId());
    }

    @PostMapping("comment/{id}/delete")
    public String deleteCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.findById(id);

        if (comment == null) {
            return "redirect:/comment/book";
        }

        Long bookId = comment.getBook().getId();
        commentService.deleteById(id);

        return "redirect:/comment/book/%d".formatted(bookId);
    }
}
