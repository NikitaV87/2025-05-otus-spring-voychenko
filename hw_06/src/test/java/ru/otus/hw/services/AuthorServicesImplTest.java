package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.models.Author;
import ru.otus.repositories.JpaAuthorRepository;
import ru.otus.services.AuthorService;
import ru.otus.services.AuthorServiceImpl;

import java.util.List;
import java.util.stream.LongStream;

@DataJpaTest
@DisplayName("Тест сервиса JpaAuthorRepository")
@Import({JpaAuthorRepository.class, AuthorServiceImpl.class})
public class AuthorServicesImplTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private TestEntityManager em;

    private List<Long> idsAuthor;

    private static List<Long> getIDsAuthor() {
        return LongStream.range(1, 4).boxed().toList();
    }

    @BeforeEach
    void setUp() {
        idsAuthor = getIDsAuthor();
    }

    @DisplayName("Должен находить все книги")
    @Test
    void getAllAuthors() {
        var expectedAuthors = idsAuthor.stream().map(id -> em.find(Author.class, id)).toList();
        var actualAuthors = authorService.findAll();

        Assertions.assertThat(actualAuthors).containsExactlyElementsOf(expectedAuthors);
    }

}
