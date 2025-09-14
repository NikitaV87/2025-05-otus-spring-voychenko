package ru.otus.mapper;

import ru.otus.domain.Book;
import ru.otus.dto.BookDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    BookDto toDto(Book book);

    List<BookDto> toDto(List<Book> books);

    Book fromDto(BookDto bookDto);
}
