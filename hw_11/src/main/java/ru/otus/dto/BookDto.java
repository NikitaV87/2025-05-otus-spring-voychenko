package ru.otus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;

    private String title;

    private AuthorDto author;

    private List<GenreDto> genres = new ArrayList<>();

    public String getGenresString() {
        return String.join(", ", genres.stream().map(GenreDto::getName).toList());
    }

    public List<Long> getGenresId() {
        return genres.stream().map(GenreDto::getId).toList();
    }
}
