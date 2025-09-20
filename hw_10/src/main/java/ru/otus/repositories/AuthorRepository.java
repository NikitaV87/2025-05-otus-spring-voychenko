package ru.otus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.models.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
