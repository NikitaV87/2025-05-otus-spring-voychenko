package ru.otus.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.dto.BookDto;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MappingBook {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    public BookDto mapToBookDto(Book book) {

        Optional<Author> author = authorRepository.findById(book.getAuthor().getId());
        List<Genre> genres = new ArrayList<>();
        book.getGenres().forEach(g -> {
            Optional<Genre> genre = genreRepository.findById(g.getId());
            genre.ifPresent(genres::add);
        });

        return new BookDto(book.getId(), book.getTitle(), author.orElse(null), genres);
    }

    public static BookDto mapToBookDtoStatic(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setGenres(book.getGenres());
        dto.setAuthor(book.getAuthor());

        return dto;
    }
}
