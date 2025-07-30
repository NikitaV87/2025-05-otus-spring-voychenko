package ru.otus.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value="comment-book-graph")
    Optional<Comment> findById(Long  id);
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value="comment-book-graph")
    List<Comment> findByBookId(Long id);
}
