package ru.otus.conroller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.dto.AuthorDto;
import ru.otus.mapper.AuthorMapper;
import ru.otus.repositories.AuthorRepository;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {
    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    @GetMapping("/api/author")
    public Flux<AuthorDto> getAuthors() {
        return authorRepository.findAll().map(authorMapper::toDto);
    }
}
