package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с автором ")
@JdbcTest
@Import(JdbcAuthorRepository.class)
public class JdbcAuthorRepositoryTest {
    @Autowired
    private JdbcAuthorRepository repositoryJdbc;

    private List<Author> dbAuthors;

    private static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id,
                        "Author_" + id
                ))
                .toList();
    }

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("должен загружать автора по id")
    @MethodSource("getDbAuthors")
    @ParameterizedTest
    void findByIdTest(Author  expectedAuthor) {
        var actualBook = repositoryJdbc.findById(expectedAuthor.getId());
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualAuthors = repositoryJdbc.findAll();
        var expectedAuthors = dbAuthors;

        assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }
}
