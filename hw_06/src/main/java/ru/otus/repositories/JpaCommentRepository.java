package ru.otus.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.otus.models.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
public class JpaCommentRepository implements CommentRepository {

    @PersistenceContext
    private final EntityManager em;

    public JpaCommentRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("comment-book-graph");

        Map<String, Object> properties = new HashMap<>();
        properties.put(FETCH.getKey(), entityGraph);

        return Optional.ofNullable(em.find(Comment.class, id, properties));
    }

    @Override
    public List<Comment> findByBookId(Long id) {
        EntityGraph<?> entityGraph = em.getEntityGraph("comment-book-graph");
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.book.id = :id",
                Comment.class);
        query.setHint(FETCH.getKey(), entityGraph);
        query.setParameter("id", id);

        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        }

        return em.merge(comment);
    }

    @Override
    public void delete(Comment comment) {
        Optional<Comment> bookCommentForDelete = Optional.ofNullable(
                em.find(Comment.class, comment.getId()));

        bookCommentForDelete.ifPresent(em::remove);
    }
}
