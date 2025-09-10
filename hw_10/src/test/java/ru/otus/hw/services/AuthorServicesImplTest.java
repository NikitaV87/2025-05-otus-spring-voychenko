package ru.otus.hw.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.AuthorDto;
import ru.otus.models.Author;
import ru.otus.services.AuthorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@DisplayName("Тест сервиса AuthorServicesImpl")
@SpringBootTest
@Transactional(propagation = Propagation.NEVER)
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
        List<AuthorDto> actualAuthors = authorService.findAll();

        Assertions.assertThat(actualAuthors).usingRecursiveComparison().isEqualTo(authors);
    }

    private static List<Author> getAuthors() {
        List<Long> authorIds = LongStream.range(1, 4).boxed().toList();
        List<Author> authors = new ArrayList<>();

        for (Long authorId : authorIds) {
            Author author = new Author();
            author.setId(authorId);
            author.setFullName("Author_" + authorId);
            authors.add(author);
        }

        return authors;
    }
}
