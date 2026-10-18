package org.example.rest;

import jakarta.validation.Valid;
import org.example.rest.dto.AuthorDto;
import org.example.rest.dto.AuthorUpsertRequest;
import org.example.rest.mapper.AuthorMapper;
import org.example.service.AuthorService;
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
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public List<AuthorDto> findAll() {
        return authorService.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public AuthorDto findById(@PathVariable long id) {
        return AuthorMapper.toDto(authorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> create(@Valid @RequestBody AuthorUpsertRequest request) {
        AuthorDto created = AuthorMapper.toDto(authorService.insert(request.fullName()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public AuthorDto update(@PathVariable long id, @Valid @RequestBody AuthorUpsertRequest request) {
        return AuthorMapper.toDto(authorService.update(id, request.fullName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        authorService.deleteById(id);
    }
}
