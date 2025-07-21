package ru.otus.hw.repositories;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.models.Author;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.JpaAuthorRepository;

import java.util.List;
import java.util.stream.LongStream;

@DisplayName("Репозиторий на основе JPA для работы с автором ")
@DataJpaTest
@Import(JpaAuthorRepository.class)
public class JpaAuthorRepositoryTest {

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
        val expectedAuthor = em.find(Author.class, expectedId);
        em.detach(expectedAuthor);

        val actualAuthor = authorRepository.findById(expectedId);

        Assertions.assertThat(actualAuthor).isPresent().get()
                .usingRecursiveComparison().isEqualTo(expectedAuthor);
    }


    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectBooksList() {
        var expectedAuthors = idsAuthor.stream().map(id -> em.find(Author.class, id)).toList();
        var actualAuthors = authorRepository.findAll();

        Assertions.assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }
}
