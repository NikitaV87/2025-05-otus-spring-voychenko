package ru.otus.repositories;

import ru.otus.domain.Author;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuthorRepository extends ReactiveMongoRepository<Author, String> {
}
