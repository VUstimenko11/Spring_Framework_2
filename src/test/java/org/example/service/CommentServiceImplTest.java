package org.example.service;

import org.example.domain.Author;
import org.example.domain.Book;
import org.example.domain.Comment;
import org.example.domain.Genre;
import org.example.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CommentServiceImpl")
class CommentServiceImplTest {

    private InMemoryCommentRepository commentRepository;
    private StubBookService bookService;
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        commentRepository = new InMemoryCommentRepository();
        bookService = new StubBookService();
        commentService = new CommentServiceImpl(commentRepository, bookService);

        bookService.put(new Book(1L, "War and Peace", new Author(1L, "Leo Tolstoy"), new Genre(1L, "Novel")));
        bookService.put(new Book(2L, "Anna Karenina", new Author(1L, "Leo Tolstoy"), new Genre(1L, "Novel")));
    }

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
        commentService.insert("First", 1L);
        commentService.insert("Second", 1L);
        commentService.insert("Other", 2L);

        assertThat(commentService.findByBookId(1L)).hasSize(2);
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

    private static final class StubBookService implements BookService {
        private final Map<Long, Book> books = new ConcurrentHashMap<>();

        void put(Book book) {
            books.put(book.getId(), book);
        }

        @Override
        public Book insert(String title, long authorId, long genreId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Book findById(long id) {
            Book book = books.get(id);
            if (book == null) {
                throw new EntityNotFoundException("Book not found: id=" + id);
            }
            return book;
        }

        @Override
        public List<Book> findAll() {
            return List.copyOf(books.values());
        }

        @Override
        public Book update(long id, String title, long authorId, long genreId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteById(long id) {
            throw new UnsupportedOperationException();
        }
    }

    private static final class InMemoryCommentRepository implements CommentRepository {
        private final AtomicLong idGenerator = new AtomicLong(1);
        private final Map<Long, Comment> comments = new ConcurrentHashMap<>();

        @Override
        public Comment insert(Comment comment) {
            long id = idGenerator.getAndIncrement();
            Comment stored = new Comment(id, comment.getText(), comment.getBook());
            comments.put(id, stored);
            comment.setId(id);
            return stored;
        }

        @Override
        public Optional<Comment> findById(long id) {
            return Optional.ofNullable(comments.get(id));
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
        public void update(Comment comment) {
            comments.put(comment.getId(), new Comment(comment.getId(), comment.getText(), comment.getBook()));
        }

        @Override
        public void deleteById(long id) {
            comments.remove(id);
        }
    }
}
