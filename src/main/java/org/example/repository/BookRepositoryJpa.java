package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.domain.Book;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepositoryJpa implements BookRepository {

    private final EntityManager entityManager;

    public BookRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Book insert(Book book) {
        entityManager.persist(book);
        return book;
    }

    @Override
    public Optional<Book> findById(long id) {
        List<Book> books = entityManager.createQuery(
                        """
                                select b from Book b
                                join fetch b.author
                                join fetch b.genre
                                where b.id = :id
                                """,
                        Book.class)
                .setParameter("id", id)
                .getResultList();
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery(
                        """
                                select b from Book b
                                join fetch b.author
                                join fetch b.genre
                                order by b.id
                                """,
                        Book.class)
                .getResultList();
    }

    @Override
    public void update(Book book) {
        entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }
}
