package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.domain.Author;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorRepositoryJpa implements AuthorRepository {

    private final EntityManager entityManager;

    public AuthorRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Author insert(Author author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(entityManager.find(Author.class, id));
    }

    @Override
    public List<Author> findAll() {
        return entityManager.createQuery("select a from Author a order by a.id", Author.class)
                .getResultList();
    }

    @Override
    public void update(Author author) {
        entityManager.merge(author);
    }

    @Override
    public void deleteById(long id) {
        Author author = entityManager.find(Author.class, id);
        if (author != null) {
            entityManager.remove(author);
        }
    }
}
