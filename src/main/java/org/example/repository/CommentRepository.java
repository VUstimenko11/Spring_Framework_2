package org.example.repository;

import org.example.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment insert(Comment comment);

    Optional<Comment> findById(long id);

    List<Comment> findAll();

    List<Comment> findByBookId(long bookId);

    void update(Comment comment);

    void deleteById(long id);
}
