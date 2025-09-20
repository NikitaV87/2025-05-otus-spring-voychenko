package ru.otus.mapper;

import org.mapstruct.Mapper;
import ru.otus.dto.BookDto;
import ru.otus.models.Book;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    BookDto toDto(Book book);

    List<BookDto> toDto(List<Book> books);

    Book fromDto(BookDto bookDto);
}
