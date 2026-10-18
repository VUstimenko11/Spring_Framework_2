package org.example.service;

import org.example.domain.Genre;
import org.example.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    @Transactional
    public Genre insert(String name) {
        return genreRepository.save(new Genre(null, name));
    }

    @Override
    @Transactional(readOnly = true)
    public Genre findById(long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    @Transactional
    public Genre update(long id, String name) {
        Genre genre = findById(id);
        genre.setName(name);
        return genreRepository.save(genre);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        findById(id);
        genreRepository.deleteById(id);
    }
}
