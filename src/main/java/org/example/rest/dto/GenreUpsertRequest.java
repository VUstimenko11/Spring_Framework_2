package org.example.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record GenreUpsertRequest(
        @NotBlank String name
) {
}
