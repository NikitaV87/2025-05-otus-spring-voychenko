package ru.otus.conroller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.dto.GenreDto;
import ru.otus.mapper.GenreMapper;
import ru.otus.repositories.GenreRepository;

@RestController
@RequiredArgsConstructor
public class GenreRestController {
    private final GenreRepository genreRepository;

    private final GenreMapper mapper;

    @GetMapping("/api/genre")
    public Flux<GenreDto> getGenreList() {
        return genreRepository.findAll().map(mapper::toDto);
    }
}
