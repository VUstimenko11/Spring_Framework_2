package org.example.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentUpsertRequest(
        @NotBlank @Size(max = 1000) String text,
        @NotNull Long bookId
) {
}
