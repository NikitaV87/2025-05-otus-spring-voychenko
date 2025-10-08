package ru.otus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookUpdateDto {

    @NotNull(message = "Must fill Book id")
    private String id;

    @NotBlank(message = "Must fill name of book")
    private String title;

    @NotNull(message = "Must select author of book")
    private String authorId;

    @NotEmpty(message = "Must choice genre of book")
    @NotNull(message = "Genre not init, please try again")
    @Builder.Default
    private List<String> genreIds = new ArrayList<>();
}
