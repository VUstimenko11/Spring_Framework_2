package org.example.repository;

import jakarta.persistence.EntityManager;
import org.example.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryJpa implements CommentRepository {

    private final EntityManager entityManager;

    public CommentRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Comment insert(Comment comment) {
        entityManager.persist(comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(long id) {
        List<Comment> comments = entityManager.createQuery(
                        """
                                select c from Comment c
                                join fetch c.book
                                where c.id = :id
                                """,
                        Comment.class)
                .setParameter("id", id)
                .getResultList();
        return comments.stream().findFirst();
    }

    @Override
    public List<Comment> findAll() {
        return entityManager.createQuery(
                        """
                                select c from Comment c
                                join fetch c.book
                                order by c.id
                                """,
                        Comment.class)
                .getResultList();
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        return entityManager.createQuery(
                        """
                                select c from Comment c
                                join fetch c.book
                                where c.book.id = :bookId
                                order by c.id
                                """,
                        Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public void update(Comment comment) {
        entityManager.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = entityManager.find(Comment.class, id);
        if (comment != null) {
            entityManager.remove(comment);
        }
    }
}