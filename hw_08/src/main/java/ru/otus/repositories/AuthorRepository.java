package ru.otus.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.models.Author;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
