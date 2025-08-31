package ru.otus.mapper.response;

import ru.otus.dto.BookDto;
import ru.otus.dto.response.ResponseCreateAndUpdateBook;

public class ResponseCreateOrUpdateBookMapper {

    public static ResponseCreateAndUpdateBook toResponse(BookDto book) {
        return ResponseCreateAndUpdateBook.builder().id(book.getId()).author(book.getAuthor().getId())
                .title(book.getTitle()).genres(book.getGenresId()).build();
    }
}
