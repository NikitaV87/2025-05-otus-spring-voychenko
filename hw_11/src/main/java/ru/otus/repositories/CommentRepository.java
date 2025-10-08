package ru.otus.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.domain.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findByBookId(String id);

    @NotNull
    Mono<Comment> findById(@NotNull String id);
}
