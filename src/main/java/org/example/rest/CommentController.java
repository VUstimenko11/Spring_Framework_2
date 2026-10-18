package org.example.rest;

import jakarta.validation.Valid;
import org.example.rest.dto.CommentDto;
import org.example.rest.dto.CommentUpsertRequest;
import org.example.rest.mapper.CommentMapper;
import org.example.service.CommentService;
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
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public List<CommentDto> findAll() {
        return commentService.findAll().stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @GetMapping("/comments/{id}")
    public CommentDto findById(@PathVariable long id) {
        return CommentMapper.toDto(commentService.findById(id));
    }

    @GetMapping("/books/{bookId}/comments")
    public List<CommentDto> findByBookId(@PathVariable long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CommentUpsertRequest request) {
        CommentDto created = CommentMapper.toDto(
                commentService.insert(request.text(), request.bookId())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/comments/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/comments/{id}")
    public CommentDto update(@PathVariable long id, @Valid @RequestBody CommentUpsertRequest request) {
        return CommentMapper.toDto(
                commentService.update(id, request.text(), request.bookId())
        );
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        commentService.deleteById(id);
    }
}
