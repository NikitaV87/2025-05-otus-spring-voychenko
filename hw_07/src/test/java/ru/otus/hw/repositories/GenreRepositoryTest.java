package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.models.Genre;
import ru.otus.repositories.GenreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанром ")
@DataJpaTest
public class GenreRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private GenreRepository genreRepository;

    private List<Long> idsGenre;

    private static List<Long> getIdsGenre() {
        return LongStream.range(1, 7).boxed().toList();
    }

    @BeforeEach
    void setUp() {
        idsGenre = getIdsGenre();
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void findByIdTest() {
        List<Long> actualIds = List.of(1L, 3L, 5L);
        var actualGenres = genreRepository.findAllByIdIn(actualIds);

        List<Genre> expectedGenres = new ArrayList<>();

        for (Long id: actualIds) {
            expectedGenres.add(em.find(Genre.class, id));
        }

        assertThat(actualGenres).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = idsGenre.stream().map(id -> em.find(Genre.class, id)).toList();

        assertThat(actualGenres).usingRecursiveComparison().isEqualTo(expectedGenres);
    }
}
