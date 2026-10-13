package org.example.shell;

import org.example.domain.Book;
import org.example.service.BookService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class BookCommands {

    private final BookService bookService;

    public BookCommands(BookService bookService) {
        this.bookService = bookService;
    }

    @ShellMethod(value = "Find all books", key = {"book-find-all", "books"})
    public String findAll() {
        return bookService.findAll().stream()
                .map(this::formatBook)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Find book by id", key = "book-find")
    public String findById(@ShellOption(help = "Book id") long id) {
        return formatBook(bookService.findById(id));
    }

    @ShellMethod(value = "Insert a new book", key = "book-insert")
    public String insert(@ShellOption(help = "Book title") String title,
                         @ShellOption(help = "Author id") long authorId,
                         @ShellOption(help = "Genre id") long genreId) {
        return "Created: " + formatBook(bookService.insert(title, authorId, genreId));
    }

    @ShellMethod(value = "Update an existing book", key = "book-update")
    public String update(@ShellOption(help = "Book id") long id,
                         @ShellOption(help = "Book title") String title,
                         @ShellOption(help = "Author id") long authorId,
                         @ShellOption(help = "Genre id") long genreId) {
        return "Updated: " + formatBook(bookService.update(id, title, authorId, genreId));
    }

    @ShellMethod(value = "Delete book by id", key = "book-delete")
    public String delete(@ShellOption(help = "Book id") long id) {
        bookService.deleteById(id);
        return "Deleted book id=" + id;
    }

    private String formatBook(Book book) {
        return "id=%d, title='%s', author='%s' (id=%d), genre='%s' (id=%d)"
                .formatted(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor().getFullName(),
                        book.getAuthor().getId(),
                        book.getGenre().getName(),
                        book.getGenre().getId()
                );
    }
}
