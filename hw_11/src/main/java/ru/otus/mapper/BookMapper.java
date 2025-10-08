package ru.otus.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import org.mapstruct.Mapper;
import ru.otus.dto.BookUpdateDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    BookDto toDto(Book book);

    List<BookDto> toDto(List<Book> books);

//    Book fromDto(BookDto bookDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "genres", source = "genreIds", qualifiedByName = "getGenreFromLong")
    Book fromCreateDto(BookCreateDto bookDto);

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "genres", source = "genreIds", qualifiedByName = "getGenreFromLong")
    Book fromUpdDto(BookUpdateDto bookUpdateDto);

    @Named("getGenreFromLong")
    public static Genre getGenreFromLong(String id) {
        return Genre.builder().id(id).build();
    }
}
