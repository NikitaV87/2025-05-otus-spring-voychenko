package ru.otus.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.models.Genre;

import java.util.List;

@Repository
public class JpaGenreRepository implements GenreRepository {
    @PersistenceContext
    private final EntityManager em;

    public JpaGenreRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Genre> findAll() {
        TypedQuery<Genre> query = em.createQuery("select g from Genre g", Genre.class);

        return query.getResultList();
    }

    @Override
    public List<Genre> findAllByIds(List<Long> ids) {
        TypedQuery<Genre> query = em.createQuery("select g from Genre g where g.id in (:ids)", Genre.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }
}
