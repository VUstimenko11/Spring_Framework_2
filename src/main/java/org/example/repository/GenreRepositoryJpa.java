package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.domain.Genre;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepositoryJpa implements GenreRepository {

    private final EntityManager entityManager;

    public GenreRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Genre insert(Genre genre) {
        entityManager.persist(genre);
        return genre;
    }

    @Override
    public Optional<Genre> findById(long id) {
        return Optional.ofNullable(entityManager.find(Genre.class, id));
    }

    @Override
    public List<Genre> findAll() {
        return entityManager.createQuery("select g from Genre g order by g.id", Genre.class)
                .getResultList();
    }

    @Override
    public void update(Genre genre) {
        entityManager.merge(genre);
    }

    @Override
    public void deleteById(long id) {
        Genre genre = entityManager.find(Genre.class, id);
        if (genre != null) {
            entityManager.remove(genre);
        }
    }
}
