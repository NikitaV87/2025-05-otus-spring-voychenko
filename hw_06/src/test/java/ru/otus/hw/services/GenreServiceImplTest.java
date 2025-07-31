package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Genre;
import ru.otus.repositories.JpaGenreRepository;
import ru.otus.services.GenreService;
import ru.otus.services.GenreServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@DataJpaTest
@DisplayName("Тест сервиса GenreServiceImpl")
@Import({GenreServiceImpl.class, JpaGenreRepository.class})
@Transactional(propagation = Propagation.NEVER)
public class GenreServiceImplTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private GenreService genreService;

    private List<Genre> genres;

    private static List<Genre> getGenres() {
        List<Long> ids = LongStream.range(1, 7).boxed().toList();
        List<Genre> genres = new ArrayList<>();

        for (Long id : ids) {
            genres.add(Genre.builder().id(id).name("Genre_" + id).build());
        }

        return genres;
    }

    @BeforeEach
    void setUp() {
        genres = getGenres();
    }

    @DisplayName("Должен загружать все жанры")
    @Test
    void findAll() {
        var actualGenres = genreService.findAll();
        List<Genre> expectedGenres = genres;

        Assertions.assertThat(actualGenres).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

}
