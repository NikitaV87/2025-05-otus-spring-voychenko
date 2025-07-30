package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.models.Genre;
import ru.otus.repositories.GenreRepository;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public Set<Genre> findAll() {
        return new HashSet<>(genreRepository.findAll());
    }
}
