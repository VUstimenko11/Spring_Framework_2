package org.example.service;

import org.example.domain.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({
        CommentServiceImpl.class,
        BookServiceImpl.class,
        AuthorServiceImpl.class,
        GenreServiceImpl.class
})
@DisplayName("CommentServiceImpl")
class CommentServiceImplTest {

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("should insert comment when book exists")
    void shouldInsertComment() {
        Comment created = commentService.insert("Great book", 1L);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getText()).isEqualTo("Great book");
        assertThat(created.getBook().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should find comment by id")
    void shouldFindById() {
        Comment created = commentService.insert("Nice", 1L);

        assertThat(commentService.findById(created.getId()).getText()).isEqualTo("Nice");
    }

    @Test
    @DisplayName("should throw when comment not found")
    void shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> commentService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("should find comments by book id")
    void shouldFindByBookId() {
        int before = commentService.findByBookId(1L).size();
        commentService.insert("First extra", 1L);
        commentService.insert("Second extra", 1L);
        commentService.insert("Other book", 2L);

        assertThat(commentService.findByBookId(1L)).hasSize(before + 2);
    }

    @Test
    @DisplayName("should update comment")
    void shouldUpdateComment() {
        Comment created = commentService.insert("Old", 1L);

        Comment updated = commentService.update(created.getId(), "New", 2L);

        assertThat(updated.getText()).isEqualTo("New");
        assertThat(updated.getBook().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("should delete comment")
    void shouldDeleteComment() {
        Comment created = commentService.insert("To delete", 1L);

        commentService.deleteById(created.getId());

        assertThatThrownBy(() -> commentService.findById(created.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
