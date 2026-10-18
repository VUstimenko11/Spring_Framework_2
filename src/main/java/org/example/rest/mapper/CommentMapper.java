package org.example.rest.mapper;

import org.example.domain.Comment;
import org.example.rest.dto.CommentDto;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId()
        );
    }
}
