package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.models.Genre;
import ru.otus.repositories.JpaGenreRepository;
import ru.otus.services.GenreService;
import ru.otus.services.GenreServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

@DataJpaTest
@DisplayName("Тест сервиса JpaAuthorRepository")
@Import({GenreServiceImpl.class, JpaGenreRepository.class})
public class GenreServiceImplTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private GenreService genreService;

    private List<Long> idsGenre;

    private static List<Long> getIdsGenre() {
        return LongStream.range(1, 7).boxed().toList();
    }

    @BeforeEach
    void setUp() {
        idsGenre = getIdsGenre();
    }

    @DisplayName("Должен загружать все жанры")
    @Test
    void findAll() {
        var actualGenres = genreService.findAll();

        Set<Genre> expectedGenres = new HashSet<>();

        for (Long id: idsGenre) {
            expectedGenres.add(em.find(Genre.class, id));
        }

        Assertions.assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
    }

}
