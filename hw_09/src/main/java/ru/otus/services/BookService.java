package ru.otus.services;

import ru.otus.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<BookDto> findById(long id);

    List<BookDto> findAll();

    BookDto insert(String title, long authorId, List<Long> genresIds);

    BookDto update(Long id, String title, long authorId, List<Long> genresIds);

    void deleteById(Long id);
}
