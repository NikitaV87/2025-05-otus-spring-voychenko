package ru.otus.mapper;

import reactor.core.publisher.Flux;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDto toDto(Genre genre);

    List<GenreDto> toDto(List<Genre> genres);

    Genre fromDto(GenreDto genreDto);

    List<Genre> fromDto(List<GenreDto> genreDto);
}
