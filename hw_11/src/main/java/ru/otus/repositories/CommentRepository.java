package ru.otus.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import ru.otus.domain.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findByBookId(String id);
}
