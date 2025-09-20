package ru.otus.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.AuthorDto;
import ru.otus.services.AuthorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping("/api/author")
    public List<AuthorDto> getAuthorList() {
        return authorService.findAll();
    }
}
