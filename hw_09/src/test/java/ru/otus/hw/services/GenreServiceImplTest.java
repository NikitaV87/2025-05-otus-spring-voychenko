package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.GenreDto;
import ru.otus.services.GenreService;

import java.util.List;
import java.util.stream.LongStream;

@SpringBootTest
@DisplayName("Тест сервиса GenreServiceImpl")
@Transactional(propagation = Propagation.NEVER)
public class GenreServiceImplTest {
    @Autowired
    private GenreService genreService;

    private List<GenreDto> genres;

    @BeforeEach
    void setUp() {
        genres = getGenres();
    }

    @DisplayName("Должен загружать все жанры")
    @Test
    void findAll() {
        var actualGenres = genreService.findAll();

        List<GenreDto> expectedGenres = genres;

        Assertions.assertThat(actualGenres).containsExactlyInAnyOrderElementsOf(expectedGenres);
        Assertions.assertThat(actualGenres.stream().map(GenreDto::getName).toList()).containsExactlyInAnyOrderElementsOf(
                expectedGenres.stream().map(GenreDto::getName).toList());
    }

    private static List<Long> getIdsGenre() {
        return LongStream.range(1, 7).boxed().toList();
    }

    private static List<GenreDto> getGenres() {
        List<Long> genreIds = getIdsGenre();

        return genreIds.stream().map(genreId ->
                GenreDto.builder().id(genreId).name("Genre_" + genreId).build()).toList();
    }

}
