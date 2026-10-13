package org.example.service;

import org.example.dao.GenreDao;
import org.example.domain.Genre;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    public GenreServiceImpl(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public Genre insert(String name) {
        return genreDao.insert(new Genre(null, name));
    }

    @Override
    public Genre findById(long id) {
        return genreDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Genre not found: id=" + id));
    }

    @Override
    public List<Genre> findAll() {
        return genreDao.findAll();
    }
}
