package org.example.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorUpsertRequest(
        @NotBlank String fullName
) {
}
