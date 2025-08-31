package ru.otus.dto.request;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateAndUpdateBook {
    private Long id;

    @NotBlank(message = "Must fill name of book")
    private String title;

    @NotNull(message = "Must select author of book")
    private Long author;

    @NotEmpty(message = "Must choice genre of book")
    private List<Long> genres = new ArrayList<>();
}
