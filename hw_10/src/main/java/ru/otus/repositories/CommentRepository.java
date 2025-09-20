package ru.otus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.models.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBookId(Long id);
}
