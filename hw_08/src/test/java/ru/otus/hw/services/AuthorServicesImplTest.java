package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Author;
import ru.otus.services.AuthorService;
import ru.otus.services.AuthorServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@DataMongoTest
@Transactional(propagation = Propagation.NEVER)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Тест сервиса AuthorServicesImpl")
@Import({AuthorServiceImpl.class})
public class AuthorServicesImplTest {
    @Autowired
    private AuthorService authorService;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = getAuthors();
    }

    @DisplayName("Должен находить всех авторов")
    @Test
    void getAllAuthors() {
        var actualAuthors = authorService.findAll();

        Assertions.assertThat(actualAuthors).usingRecursiveComparison().isEqualTo(authors);
    }

    private static List<Author> getAuthors() {
        List<String> authorIds = LongStream.range(1, 4).boxed().map(Object::toString).toList();
        List<Author> authors = new ArrayList<>();

        for (String authorId : authorIds) {
            Author author = new Author();
            author.setId(authorId);
            author.setFullName("Author_" + authorId);
            authors.add(author);
        }

        return authors;
    }
}
