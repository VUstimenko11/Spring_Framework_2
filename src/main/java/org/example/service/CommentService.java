package org.example.service;

import org.example.domain.Comment;

import java.util.List;

public interface CommentService {

    Comment insert(String text, long bookId);

    Comment findById(long id);

    List<Comment> findAll();

    List<Comment> findByBookId(long bookId);

    Comment update(long id, String text, long bookId);

    void deleteById(long id);
}
