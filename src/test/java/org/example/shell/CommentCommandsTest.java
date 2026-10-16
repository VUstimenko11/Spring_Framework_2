package org.example.shell;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Comment;
import org.example.domain.Genre;
import org.example.service.CommentService;
import org.example.service.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentCommands")
class CommentCommandsTest {

    private StubCommentService commentService;
    private CommentCommands commentCommands;

    @BeforeEach
    void setUp() {
        commentService = new StubCommentService();
        commentCommands = new CommentCommands(commentService);
        Book book = new Book(1L, "War and Peace", new Author(1L, "Leo Tolstoy"), new Genre(1L, "Novel"));
        commentService.put(new Comment(1L, "A masterpiece", book));
        commentService.put(new Comment(2L, "Worth reading", book));
    }

    @Test
    @DisplayName("should format comment list")
    void shouldFindAll() {
        String result = commentCommands.findAll();

        assertThat(result).contains("A masterpiece", "Worth reading");
    }

    @Test
    @DisplayName("should format single comment")
    void shouldFindById() {
        assertThat(commentCommands.findById(1L)).contains("A masterpiece");
    }

    @Test
    @DisplayName("should format comments by book")
    void shouldFindByBookId() {
        assertThat(commentCommands.findByBookId(1L)).contains("A masterpiece", "Worth reading");
    }

    @Test
    @DisplayName("should insert comment and return created message")
    void shouldInsert() {
        assertThat(commentCommands.insert("New text", 1L)).startsWith("Created:");
    }

    @Test
    @DisplayName("should update comment and return updated message")
    void shouldUpdate() {
        assertThat(commentCommands.update(1L, "Updated", 1L)).startsWith("Updated:");
    }

    @Test
    @DisplayName("should delete comment")
    void shouldDelete() {
        String result = commentCommands.delete(1L);

        assertThat(result).isEqualTo("Deleted comment id=1");
        assertThat(commentService.wasDeleted()).isTrue();
    }

    private static final class StubCommentService implements CommentService {
        private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
        private final AtomicLong idGenerator = new AtomicLong(100);
        private final AtomicBoolean deleted = new AtomicBoolean(false);

        void put(Comment comment) {
            comments.put(comment.getId(), comment);
        }

        boolean wasDeleted() {
            return deleted.get();
        }

        @Override
        public Comment insert(String text, long bookId) {
            Book book = new Book(bookId, "Title", new Author(1L, "Author"), new Genre(1L, "Genre"));
            Comment comment = new Comment(idGenerator.getAndIncrement(), text, book);
            comments.put(comment.getId(), comment);
            return comment;
        }

        @Override
        public Comment findById(long id) {
            Comment comment = comments.get(id);
            if (comment == null) {
                throw new EntityNotFoundException("Comment not found: id=" + id);
            }
            return comment;
        }

        @Override
        public List<Comment> findAll() {
            return new ArrayList<>(comments.values());
        }

        @Override
        public List<Comment> findByBookId(long bookId) {
            return comments.values().stream()
                    .filter(comment -> comment.getBook().getId().equals(bookId))
                    .collect(Collectors.toList());
        }

        @Override
        public Comment update(long id, String text, long bookId) {
            Book book = new Book(bookId, "Title", new Author(1L, "Author"), new Genre(1L, "Genre"));
            Comment comment = new Comment(id, text, book);
            comments.put(id, comment);
            return comment;
        }

        @Override
        public void deleteById(long id) {
            comments.remove(id);
            deleted.set(true);
        }
    }
}
