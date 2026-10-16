package org.example.repository;

import org.example.domain.Book;
import org.example.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({CommentRepositoryJpa.class, BookRepositoryJpa.class})
@DisplayName("CommentRepositoryJpa")
class CommentRepositoryJpaTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("should find all seeded comments with book")
    void shouldFindAllComments() {
        List<Comment> comments = commentRepository.findAll();

        assertThat(comments).hasSizeGreaterThanOrEqualTo(5);
        assertThat(comments.get(0).getBook()).isNotNull();
    }

    @Test
    @DisplayName("should find comments by book id")
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
    @DisplayName("should insert comment")
    void shouldInsertComment() {
        Book book = bookRepository.findById(1L).orElseThrow();

        Comment inserted = commentRepository.insert(new Comment(null, "New comment", book));

        assertThat(inserted.getId()).isNotNull();
        assertThat(commentRepository.findById(inserted.getId()))
                .isPresent()
                .get()
                .extracting(Comment::getText)
                .isEqualTo("New comment");
    }

    @Test
    @DisplayName("should update comment")
    void shouldUpdateComment() {
        Book book = bookRepository.findById(2L).orElseThrow();
        Comment comment = commentRepository.insert(new Comment(null, "Temp", book));

        comment.setText("Updated comment");
        comment.setBook(bookRepository.findById(1L).orElseThrow());
        commentRepository.update(comment);

        Comment updated = commentRepository.findById(comment.getId()).orElseThrow();
        assertThat(updated.getText()).isEqualTo("Updated comment");
        assertThat(updated.getBook().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should delete comment by id")
    void shouldDeleteComment() {
        Book book = bookRepository.findById(1L).orElseThrow();
        Comment comment = commentRepository.insert(new Comment(null, "To Delete", book));

        commentRepository.deleteById(comment.getId());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }
}
