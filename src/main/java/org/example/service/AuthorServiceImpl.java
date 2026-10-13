package org.example.service;

import org.example.dao.AuthorDao;
import org.example.domain.Author;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    public Author insert(String fullName) {
        return authorDao.insert(new Author(null, fullName));
    }

    @Override
    public Author findById(long id) {
        return authorDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: id=" + id));
    }

    @Override
    public List<Author> findAll() {
        return authorDao.findAll();
    }
}
