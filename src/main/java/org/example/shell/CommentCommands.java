package org.example.shell;

import org.example.domain.Comment;
import org.example.service.CommentService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    public CommentCommands(CommentService commentService) {
        this.commentService = commentService;
    }

    @ShellMethod(value = "Find all comments", key = {"comment-find-all", "comments"})
    public String findAll() {
        return commentService.findAll().stream()
                .map(this::formatComment)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "comment-find")
    public String findById(@ShellOption(help = "Comment id") long id) {
        return formatComment(commentService.findById(id));
    }

    @ShellMethod(value = "Find comments by book id", key = "comment-find-by-book")
    public String findByBookId(@ShellOption(help = "Book id") long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(this::formatComment)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new comment", key = "comment-insert")
    public String insert(@ShellOption(help = "Comment text") String text,
                         @ShellOption(help = "Book id") long bookId) {
        return "Created: " + formatComment(commentService.insert(text, bookId));
    }

    @ShellMethod(value = "Update an existing comment", key = "comment-update")
    public String update(@ShellOption(help = "Comment id") long id,
                         @ShellOption(help = "Comment text") String text,
                         @ShellOption(help = "Book id") long bookId) {
        return "Updated: " + formatComment(commentService.update(id, text, bookId));
    }

    @ShellMethod(value = "Delete comment by id", key = "comment-delete")
    public String delete(@ShellOption(help = "Comment id") long id) {
        commentService.deleteById(id);
        return "Deleted comment id=" + id;
    }

    private String formatComment(Comment comment) {
        return "id=%d, text='%s', bookId=%d"
                .formatted(comment.getId(), comment.getText(), comment.getBook().getId());
    }
}
