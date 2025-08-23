package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.models.Author;
import ru.otus.models.Genre;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private String id;

    private String title;

    private Author author;

    private List<Genre> genres = new ArrayList<>();
}
