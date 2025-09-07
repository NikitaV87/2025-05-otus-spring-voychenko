package ru.otus.mapper;

import org.mapstruct.Mapper;
import ru.otus.dto.GenreDto;
import ru.otus.models.Genre;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDto toDto(Genre genre);

    List<GenreDto> toDto(List<Genre> genres);

    Genre fromDto(GenreDto genreDto);

    List<Genre> fromDto(List<GenreDto> genreDto);
}
