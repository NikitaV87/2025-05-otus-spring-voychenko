package ru.otus.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Must specify the title of the book")
    private String title;

    @Valid
    private AuthorDto author;

    @Valid
    private List<GenreDto> genres = new ArrayList<>();

    public String getGenresString() {
        return String.join(", ", genres.stream().map(GenreDto::getName).toList());
    }

    public List<Long> getGenresId() {
        return genres.stream().map(GenreDto::getId).toList();
    }
}
