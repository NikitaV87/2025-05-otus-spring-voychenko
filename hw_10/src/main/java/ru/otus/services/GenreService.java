package ru.otus.services;

import ru.otus.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();
}
