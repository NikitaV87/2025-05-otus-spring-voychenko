package ru.otus.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.models.Book;
import ru.otus.models.BookComment;

import java.util.Collections;
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

    @Override
    public List<BookComment> findByBookId(Long id) {
        Optional<Book> book = Optional.ofNullable(em.find(Book.class, id));

        if (book.isEmpty()) {
            return Collections.emptyList();
        }

        return book.get().getComments();
    }

    @Override
    public BookComment save(BookComment bookComment) {
        if (bookComment.getId() == null) {
            em.persist(bookComment);
            return bookComment;
        }

        return em.merge(bookComment);
    }

    @Override
    public void delete(BookComment bookComment) {
        Optional<BookComment> bookCommentForDelete = Optional.ofNullable(
                em.find(BookComment.class, bookComment.getId()));

        bookCommentForDelete.ifPresent(em::remove);
    }
}
