package org.example.service;

import org.example.domain.Book;
import org.example.domain.Comment;
import org.example.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookService bookService;

    public CommentServiceImpl(CommentRepository commentRepository, BookService bookService) {
        this.commentRepository = commentRepository;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public Comment insert(String text, long bookId) {
        Book book = bookService.findById(bookId);
        return commentRepository.save(new Comment(null, text, book));
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: id=" + id));
        initializeBook(comment);
        return comment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findAll() {
        List<Comment> comments = commentRepository.findAll();
        comments.forEach(this::initializeBook);
        return comments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(long bookId) {
        bookService.findById(bookId);
        List<Comment> comments = commentRepository.findByBookId(bookId);
        comments.forEach(this::initializeBook);
        return comments;
    }

    @Override
    @Transactional
    public Comment update(long id, String text, long bookId) {
        Comment comment = findById(id);
        Book book = bookService.findById(bookId);
        comment.setText(text);
        comment.setBook(book);
        commentRepository.save(comment);
        return findById(id);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        findById(id);
        commentRepository.deleteById(id);
    }

    private void initializeBook(Comment comment) {
        comment.getBook().getId();
    }
}
