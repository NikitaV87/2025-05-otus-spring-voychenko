package ru.otus.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.domain.Genre;

public interface GenreRepository extends ReactiveMongoRepository<Genre, String> {
}
