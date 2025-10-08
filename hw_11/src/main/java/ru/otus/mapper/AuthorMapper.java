package ru.otus.mapper;

import ru.otus.domain.Author;
import ru.otus.dto.AuthorDto;
import org.mapstruct.Mapper;


import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDto toDto(Author author);

    List<AuthorDto> toDto(List<Author> authors);

    Author fromDto(AuthorDto authorDto);
}
