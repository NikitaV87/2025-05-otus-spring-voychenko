package ru.otus.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthorController {
    @GetMapping("/author")
    public String getAuthorView() {
        return "author/author";
    }
}
