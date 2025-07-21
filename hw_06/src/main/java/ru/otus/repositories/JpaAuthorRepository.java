package ru.otus.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAuthorRepository implements AuthorRepository {
    @PersistenceContext
    private final EntityManager em;

    public JpaAuthorRepository(EntityManager em) {
        this.em = em;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> findById(Long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Author> findAll() {
        TypedQuery<Author> query = em.createQuery("select a from Author a", Author.class);
        return query.getResultList();
    }
}
