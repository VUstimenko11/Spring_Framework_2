package org.example.shell;

import org.example.domain.Author;
import org.example.service.AuthorService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    public AuthorCommands(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ShellMethod(value = "Find all authors", key = {"author-find-all", "authors"})
    public String findAll() {
        return authorService.findAll().stream()
                .map(this::formatAuthor)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new author", key = "author-insert")
    public String insert(@ShellOption(help = "Author full name") String fullName) {
        return "Created: " + formatAuthor(authorService.insert(fullName));
    }

    private String formatAuthor(Author author) {
        return "id=%d, fullName='%s'".formatted(author.getId(), author.getFullName());
    }
}
