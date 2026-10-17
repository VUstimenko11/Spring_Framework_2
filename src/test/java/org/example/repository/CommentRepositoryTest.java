package org.example.repository;

import org.example.domain.Book;
import org.example.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("CommentRepository")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("should find all seeded comments")
    void shouldFindAllComments() {
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("should find comments by book id via derived query")
    void shouldFindByBookId() {
        List<Comment> comments = commentRepository.findByBookId(1L);

        assertThat(comments).hasSizeGreaterThanOrEqualTo(2);
        assertThat(comments).allMatch(comment -> comment.getBook().getId().equals(1L));
    }

    @Test
    @DisplayName("should find comment by id")
    void shouldFindById() {
        Comment comment = commentRepository.findById(1L).orElseThrow();

        assertThat(comment.getText()).isEqualTo("A masterpiece of world literature");
        assertThat(comment.getBook().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should save comment")
    void shouldSaveComment() {
        Book book = bookRepository.findByIdWithDetails(1L).orElseThrow();

        Comment saved = commentRepository.save(new Comment(null, "New comment", book));

        assertThat(saved.getId()).isNotNull();
        assertThat(commentRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Comment::getText)
                .isEqualTo("New comment");
    }

    @Test
    @DisplayName("should update comment")
    void shouldUpdateComment() {
        Book book = bookRepository.findByIdWithDetails(2L).orElseThrow();
        Comment comment = commentRepository.save(new Comment(null, "Temp", book));

        comment.setText("Updated comment");
        comment.setBook(bookRepository.findByIdWithDetails(1L).orElseThrow());
        commentRepository.save(comment);

        Comment updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getText()).isEqualTo("Updated comment");
        assertThat(updated.getBook().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete comment by id")
    void shouldDeleteComment() {
        Book book = bookRepository.findByIdWithDetails(1L).orElseThrow();
        Comment comment = commentRepository.save(new Comment(null, "To Delete", book));

        commentRepository.deleteById(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
