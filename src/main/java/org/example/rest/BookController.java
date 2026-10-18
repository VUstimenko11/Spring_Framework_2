package org.example.rest;

import jakarta.validation.Valid;
import org.example.rest.dto.BookDto;
import org.example.rest.dto.BookUpsertRequest;
import org.example.rest.mapper.BookMapper;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> findAll() {
        return bookService.findAll().stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public BookDto findById(@PathVariable long id) {
        return BookMapper.toDto(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookUpsertRequest request) {
        BookDto created = BookMapper.toDto(
                bookService.insert(request.title(), request.authorId(), request.genreId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable long id, @Valid @RequestBody BookUpsertRequest request) {
        return BookMapper.toDto(
                bookService.update(id, request.title(), request.authorId(), request.genreId())
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        bookService.deleteById(id);
    }
}
