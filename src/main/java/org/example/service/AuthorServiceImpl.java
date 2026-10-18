package org.example.service;

import org.example.domain.Author;
import org.example.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public Author insert(String fullName) {
        return authorRepository.save(new Author(null, fullName));
    }

    @Override
    @Transactional(readOnly = true)
    public Author findById(long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    @Transactional
    public Author update(long id, String fullName) {
        Author author = findById(id);
        author.setFullName(fullName);
        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        findById(id);
        authorRepository.deleteById(id);
    }
}
