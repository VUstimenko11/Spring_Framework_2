package org.example.rest;

import jakarta.validation.Valid;
import org.example.rest.dto.GenreDto;
import org.example.rest.dto.GenreUpsertRequest;
import org.example.rest.mapper.GenreMapper;
import org.example.service.GenreService;
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
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreDto> findAll() {
        return genreService.findAll().stream()
                .map(GenreMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable long id) {
        return GenreMapper.toDto(genreService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GenreDto> create(@Valid @RequestBody GenreUpsertRequest request) {
        GenreDto created = GenreMapper.toDto(genreService.insert(request.name()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public GenreDto update(@PathVariable long id, @Valid @RequestBody GenreUpsertRequest request) {
        return GenreMapper.toDto(genreService.update(id, request.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        genreService.deleteById(id);
    }
}
