package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Genre;
import ru.otus.services.GenreService;
import ru.otus.services.GenreServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@DataMongoTest
@DisplayName("Тест сервиса GenreServiceImpl")
@Import({GenreServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    private Set<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = getGenres();
    }

    @DisplayName("Должен загружать все жанры")
    @Test
    void findAll() {
        var actualGenres = genreService.findAll();

        Set<Genre> expectedGenres = genres;

        Assertions.assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
        Assertions.assertThat(actualGenres.stream().map(Genre::getName).toList()).containsExactlyInAnyOrderElementsOf(
                expectedGenres.stream().map(Genre::getName).toList());
    }

    private static List<String> getIdsGenre() {
        return LongStream.range(1, 7).boxed().map(Object::toString).toList();
    }

    private static Set<Genre> getGenres() {
        List<String> genreIds = getIdsGenre();

        return genreIds.stream().map(genreId ->
                Genre.builder().id(genreId).name("Genre_" + genreId).build()).collect(Collectors.toSet());
    }

}
