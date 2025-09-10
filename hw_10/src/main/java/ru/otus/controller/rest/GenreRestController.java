package ru.otus.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.GenreDto;
import ru.otus.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenreRestController {
    private final GenreService genreService;

    @GetMapping("/api/genre")
    public List<GenreDto> getGenreList() {
        return genreService.findAll();
    }
}
