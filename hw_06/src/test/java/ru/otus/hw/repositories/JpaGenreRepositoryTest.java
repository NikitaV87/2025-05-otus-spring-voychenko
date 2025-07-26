package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.TestUtils;
import ru.otus.models.Genre;
import ru.otus.repositories.GenreRepository;
import ru.otus.repositories.JpaGenreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@DisplayName("Репозиторий на основе Jpa для работы с жанром ")
@DataJpaTest
@Import({JpaGenreRepository.class})
public class JpaGenreRepositoryTest {
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
        Set<Long> actualIds = Set.of(1L, 3L, 5L);
        var actualGenres = genreRepository.findAllByIds(actualIds);

        Set<Genre> expectedGenres = new HashSet<>();

        for (Long id: actualIds) {
            expectedGenres.add(em.find(Genre.class, id));
        }

        TestUtils.equalGenres(actualGenres, expectedGenres);
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = idsGenre.stream().map(id -> em.find(Genre.class, id))
                .collect(Collectors.toSet());

        TestUtils.equalGenres(actualGenres, expectedGenres);
    }
}
