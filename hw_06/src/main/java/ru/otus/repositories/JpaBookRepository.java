package ru.otus.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Book;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaBookRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-author-genre-graph");

        Map<String, Object> properties = new HashMap<>();
        properties.put(FETCH.getKey(), entityGraph);
        Optional<Book> book =  Optional.ofNullable(em.find(Book.class, id, properties));

        if (book.isPresent()) {
            entityGraph = em.getEntityGraph("book-comment-graph");
            book = Optional.ofNullable(em.createQuery("select distinct b from Book b " +
                            "left join fetch b.comments c " +
                            "where b in :book", Book.class)
                    .setParameter("book", book.get())
                    .setHint(FETCH.getKey(), entityGraph)
                    .getSingleResult());
        }
        return book;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph("book-author-genre-graph");
        TypedQuery<Book> query = em.createQuery("select distinct b from Book b " +
                "left join fetch b.genres " +
                "left join fetch b.author", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);

        List<Book> book = query.getResultList();

        if (book.size() > 0) {
            entityGraph = em.getEntityGraph("book-comment-graph");
            book = em.createQuery("select distinct b from Book b " +
                            "left join fetch b.comments c " +
                            "where b in :book", Book.class)
                    .setParameter("book", book).
                    setHint(FETCH.getKey(), entityGraph)
                    .getResultList();

        }

        return book;
    }

    @Transactional
    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        }

        return em.merge(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        Query queryBook = em.createQuery("delete from Book b where b.id = :id");
        queryBook.setParameter("id", id);
        queryBook.executeUpdate();
    }
}
