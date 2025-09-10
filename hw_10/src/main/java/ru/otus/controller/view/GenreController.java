package ru.otus.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GenreController {

    @GetMapping("/genre")
    public String getGenreView() {
        return "genre/genre";
    }
}
