package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.models.Author;
import ru.otus.repositories.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с автором ")
@DataJpaTest
public class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AuthorRepository authorRepository;

    List<Long> idsAuthor;

    private static List<Long> getIDsAuthor() {
        return LongStream.range(1, 4).boxed().toList();
    }

    @BeforeEach
    void setUp() {
        idsAuthor = getIDsAuthor();
    }

    @DisplayName("должен загружать автора по id")
    @MethodSource("getIDsAuthor")
    @ParameterizedTest
    void findByIdTest(Long  expectedId) {
        Author expectedAuthor = em.find(Author.class, expectedId);
        Optional<Author> actualAuthor = authorRepository.findById(expectedId);

        assertThat(actualAuthor).isPresent().get().usingRecursiveComparison().isEqualTo(expectedAuthor);
    }


    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Author> expectedAuthors = idsAuthor.stream().map(id -> em.find(Author.class, id)).toList();
        List<Author> actualAuthors = authorRepository.findAll();

        assertThat(actualAuthors).usingRecursiveComparison().isEqualTo(expectedAuthors);
    }
}
