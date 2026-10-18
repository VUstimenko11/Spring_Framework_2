package org.example.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookUpsertRequest(
        @NotBlank String title,
        @NotNull Long authorId,
        @NotNull Long genreId
) {
}
