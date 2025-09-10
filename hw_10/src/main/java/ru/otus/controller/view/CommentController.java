package ru.otus.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CommentController {
    @GetMapping("/comment/book")
    public String getAllBooks() {
        return "comment/book";
    }

    @GetMapping("/comment/book/{bookId}")
    public String getCommentsByBook(@PathVariable Long bookId, Model model) {
        model.addAttribute("bookId", bookId);

        return "comment/comments";
    }

    @GetMapping("/comment/{id}")
    public String getUpdateComment(@PathVariable Long id, Model model) {
        model.addAttribute("commentId", id);

        return "comment/formUpd";
    }

    @GetMapping("/comment/new/book/{id}")
    public String getNewComment(@PathVariable Long id, Model model) {
        model.addAttribute("bookId", id);

        return "comment/formCreate";
    }
}
