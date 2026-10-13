package org.example.shell;

import org.example.domain.Genre;
import org.example.service.GenreService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
public class GenreCommands {

    private final GenreService genreService;

    public GenreCommands(GenreService genreService) {
        this.genreService = genreService;
    }

    @ShellMethod(value = "Find all genres", key = {"genre-find-all", "genres"})
    public String findAll() {
        return genreService.findAll().stream()
                .map(this::formatGenre)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Insert a new genre", key = "genre-insert")
    public String insert(@ShellOption(help = "Genre name") String name) {
        return "Created: " + formatGenre(genreService.insert(name));
    }

    private String formatGenre(Genre genre) {
        return "id=%d, name='%s'".formatted(genre.getId(), genre.getName());
    }
}
