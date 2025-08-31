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
import ru.otus.dto.CommentDto;
import ru.otus.dto.request.RequestCreateOrUpdateComment;
import ru.otus.dto.response.ResponseCreateOrUpdateComment;
import ru.otus.mapper.response.ResponseCreateOrUpdateCommentMapper;
import ru.otus.services.BookService;
import ru.otus.services.CommentService;

import java.util.List;
import java.util.Optional;

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
        Optional<CommentDto> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("message", "comment not find!");
            return "error/message";
        }

        model.addAttribute("comment", ResponseCreateOrUpdateCommentMapper.toResponse(comment.get()));
        model.addAttribute("books", List.of(comment.get().getBook()));

        return "comment/form";
    }

    @PostMapping("/comment/{id}")
    public String postUpdateComment(@PathVariable Long id,
                                 @Valid @ModelAttribute("comment") RequestCreateOrUpdateComment request,
                                 BindingResult bindingResult,
                                 Model model) {
        Optional<CommentDto> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            model.addAttribute("message", "comment not find!");
            return "error/message";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("books", List.of(comment.get().getBook()));
            return "comment/form";
        }

        commentService.update(id, request.getText());

        return "redirect:/comment/book/%d".formatted(request.getBook());
    }

    @GetMapping("/comment/new/book/{bookId}")
    public String getNewComment(@PathVariable Long bookId, Model model) {

        Optional<BookDto> book = bookService.findById(bookId);

        if (book.isEmpty()) {
            model.addAttribute("message", "Book not find!");
            return "error/message";
        }

        model.addAttribute("comment", ResponseCreateOrUpdateComment.builder().book(bookId).build());
        model.addAttribute("books", List.of(book.get()));

        return "comment/form";
    }

    @PostMapping("/comment/new/book/{bookId}")
    public String postNewComment(@PathVariable Long bookId,
                                 @Valid @ModelAttribute("comment") RequestCreateOrUpdateComment request,
                                 BindingResult bindingResult,
                                 Model model) {
        Optional<BookDto> book = bookService.findById(bookId);

        if (book.isEmpty()) {
            model.addAttribute("message", "book not find!");
            return "error/message";
        }

        commentService.insert(request.getText(), request.getBook());

        return "redirect:/comment/book/%d".formatted(request.getBook());
    }

    @PostMapping("comment/{id}/delete")
    public String deleteCommentById(@PathVariable Long id) {
        Optional<CommentDto> comment = commentService.findById(id);

        if (comment.isEmpty()) {
            return "redirect:/comment/book";
        }

        Long bookId = comment.get().getBook().getId();
        commentService.deleteById(id);

        return "redirect:/comment/book/%d".formatted(bookId);
    }
}
