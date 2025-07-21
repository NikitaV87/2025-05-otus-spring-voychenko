package ru.otus.hw.services;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.models.Book;
import ru.otus.models.BookComment;
import ru.otus.models.Genre;
import ru.otus.repositories.JpaAuthorRepository;
import ru.otus.repositories.JpaBookCommentRepository;
import ru.otus.repositories.JpaBookRepository;
import ru.otus.repositories.JpaGenreRepository;
import ru.otus.services.BookService;
import ru.otus.services.BookServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Тест сервиса JpaAuthorRepository")
@Import({JpaBookRepository.class, JpaAuthorRepository.class,
        JpaGenreRepository.class, JpaBookCommentRepository.class,
        BookServiceImpl.class})
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;

    @Autowired
    private TestEntityManager em;

    private List<Long> dbBooksIds;

    public static final Long DELETE_BOOK_ID = 3L;

    public static final Long UPDATE_BOOK_ID = 1L;

    public static final String UPDATE_BOOK_TITLE = "UPDATE_BOOK_TITLE";

    public static final Long UPDATE_BOOK_AUTHOR_ID = 2L;

    public static final Set<Long> UPDATE_BOOK_GENRE_IDS = Set.of(2L);

    public static final List<String> UPDATE_BOOK_COMMENTS = List.of("UPDATE_BOOK_COMMENTS");

    public static final String NEW_BOOK_TITLE = "NEW_BOOK_TITLE";

    public static final Long NEW_BOOK_AUTHOR_ID = 1L;

    public static final Set<Long> NEW_BOOK_GENRES_IDS = Set.of(1L, 3L);

    public static final List<String> NEW_BOOK_COMMENTS = List.of("NEW_COMMENT_1", "NEW_COMMENT_2");


    @BeforeEach
    void setUp() {
        dbBooksIds = getBookIds();
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void findByIdTest(Long expectedBookId) {
        val expectedBook = em.find(Book.class, expectedBookId);
        val actualBook = bookService.findById(expectedBook.getId());

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void findAllTest() {
        val actualBooks = bookService.findAll();
        List<Book> expectedBooks = dbBooksIds.stream().map(id -> em.find(Book.class, id)).toList();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен добавлять новые книги")
    @Test
    void insertTest() {
        val expectedBook = bookService.insert(NEW_BOOK_TITLE, NEW_BOOK_AUTHOR_ID, NEW_BOOK_GENRES_IDS, NEW_BOOK_COMMENTS);
        Book actualBook = em.find(Book.class, expectedBook.getId());

        assertThat(actualBook).isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.getTitle(), NEW_BOOK_TITLE);
        Assertions.assertEquals(actualBook.getAuthor().getId(), NEW_BOOK_AUTHOR_ID);
        assertThat(actualBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(NEW_BOOK_GENRES_IDS);
        assertThat(actualBook.getComments().stream().map(BookComment::getText))
                .containsExactlyInAnyOrderElementsOf(NEW_BOOK_COMMENTS);
    }

    @DisplayName("должен обновлять книги")
    @Test
    void updateTest() {
        Book expectedBook = bookService.update(UPDATE_BOOK_ID, UPDATE_BOOK_TITLE, UPDATE_BOOK_AUTHOR_ID, UPDATE_BOOK_GENRE_IDS, UPDATE_BOOK_COMMENTS);
        Book actualBook = em.find(Book.class, UPDATE_BOOK_ID);

        assertThat(actualBook).isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.getTitle(), UPDATE_BOOK_TITLE);
        Assertions.assertEquals(actualBook.getAuthor().getId(), UPDATE_BOOK_AUTHOR_ID);
        assertThat(actualBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(UPDATE_BOOK_GENRE_IDS);
        assertThat(actualBook.getComments().stream().map(BookComment::getText))
                .containsExactlyInAnyOrderElementsOf(UPDATE_BOOK_COMMENTS);
    }

    @DisplayName("должен удалять книгу по Id")
    @Test
    void deleteByIdTest() {
        bookService.deleteById(DELETE_BOOK_ID);
        Optional<Book> deletedBook = Optional.ofNullable(em.find(Book.class, DELETE_BOOK_ID));
        assertThat(deletedBook).isEmpty();
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }
}
