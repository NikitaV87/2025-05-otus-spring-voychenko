package ru.otus.converters;

import org.springframework.stereotype.Component;
import ru.otus.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "Id: %s, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
