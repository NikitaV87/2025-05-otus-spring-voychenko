package ru.otus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.models.Genre;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAllByIdIn(List<Long> ids);
}
