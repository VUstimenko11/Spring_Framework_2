package org.example.repository;

import org.example.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            select distinct b from Book b
            join fetch b.author
            join fetch b.genre
            where b.id = :id
            """)
    Optional<Book> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            select distinct b from Book b
            join fetch b.author
            join fetch b.genre
            order by b.id
            """)
    List<Book> findAllWithDetails();
}
