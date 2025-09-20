package ru.otus.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookController {
    @GetMapping("/book")
    public String getAllBooks() {
        return "book/book";
    }

    @GetMapping("book/new")
    public String getCreateNewBook() {
        return "book/formCreate";
    }

    @GetMapping("book/{id}")
    public String getUpdateBook(@PathVariable Long id, Model model) {
        model.addAttribute("bookId", id);

        return "book/formUpd";
    }
}
