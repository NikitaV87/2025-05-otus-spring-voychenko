package ru.otus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dto.GenreDto;
import ru.otus.mapper.GenreMapper;
import ru.otus.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    public List<GenreDto> findAll() {
        return genreMapper.toDto(genreRepository.findAll());
    }
}
