package ru.otus.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookCommentRepository implements BookCommentRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaBookCommentRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<BookComment> findById(Long id) {
        return Optional.ofNullable(em.find(BookComment.class, id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookComment> findByBookId(Long id) {
        TypedQuery<BookComment> query = em.createQuery("select bc from BookComment bc where bc.book.id = :id",
                BookComment.class);
        query.setParameter("id", id);

        return query.getResultList();
    }

    @Transactional
    @Override
    public BookComment save(BookComment bookComment) {
        if (bookComment.getId() == null) {
            em.persist(bookComment);
            return bookComment;
        }

        return em.merge(bookComment);
    }

    @Transactional
    @Override
    public void delete(BookComment bookComment) {
        BookComment bookCommentForDelete = em.find(BookComment.class, bookComment.getId());
        em.remove(bookCommentForDelete);
    }
}
