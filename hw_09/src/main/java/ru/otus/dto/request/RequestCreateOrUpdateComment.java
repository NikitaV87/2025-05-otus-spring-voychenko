package ru.otus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCreateOrUpdateComment {
    private Long id;

    @NotBlank(message = "Must fill text of comment")
    private String text;

    @NotNull(message = "Must select book for comment")
    private Long book;
}
