package ru.otus.mapper;

import org.mapstruct.Mapper;
import ru.otus.dto.AuthorDto;
import ru.otus.models.Author;

import java.util.List;

//@Mapper(componentModel = "spring")
@Mapper(componentModel = "spring")
public interface AuthorMapper {
    AuthorDto toDto(Author author);

    List<AuthorDto> toDto(List<Author> authors);

    Author fromDto(AuthorDto authorDto);
}
