package ru.otus.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.otus.domain.Book;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import org.mapstruct.Mapper;
import ru.otus.dto.BookUpdateDto;
import ru.otus.dto.GenreDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {
    @Mapping(target = "author", source = "author", qualifiedByName = "getAuthorFromId")
    @Mapping(target = "genres", source = "genres", qualifiedByName = "getGenreFromId")
    BookDto toDto(Book book);

    List<BookDto> toDto(List<Book> books);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "genres", source = "genreIds")
    Book fromCreateDto(BookCreateDto bookDto);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "genres", source = "genreIds")
    Book fromUpdDto(BookUpdateDto bookUpdateDto);

    @Named("getAuthorFromId")
    public static AuthorDto getAuthorFromId(String id) {
        return AuthorDto.builder().id(id).build();
    }

    @Named("getGenreFromId")
    public static GenreDto getGenreFromId(String id) {
        return GenreDto.builder().id(id).build();
    }
}
