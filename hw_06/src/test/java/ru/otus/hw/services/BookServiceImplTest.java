package ru.otus.hw.services;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.repositories.JpaAuthorRepository;
import ru.otus.repositories.JpaCommentRepository;
import ru.otus.repositories.JpaBookRepository;
import ru.otus.repositories.JpaGenreRepository;
import ru.otus.services.BookService;
import ru.otus.services.BookServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест сервиса BookServiceImpl")
@Import({JpaBookRepository.class, JpaAuthorRepository.class,
        JpaGenreRepository.class, JpaCommentRepository.class,
        BookServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;

    private Map<Long, Book> books;

    private static final Map<Long, Long> BOOK_ID_CONTAINS_AUTHOR_ID = Map.of(1L,1L, 2L, 2L, 3L, 3L);

    private static final Map<Long, List<Long>> BOOK_ID_CONTAINS_GENRE_ID = Map.of(1L, List.of(1L, 2L),
            2L, List.of(3L, 4L), 3L, List.of(5L, 6L));

    public static final Long DELETE_BOOK_ID = 3L;

    public static final Long UPDATE_BOOK_ID = 1L;

    public static final String UPDATE_BOOK_TITLE = "UPDATE_BOOK_TITLE";

    public static final Long UPDATE_BOOK_AUTHOR_ID = 2L;

    public static final List<Long> UPDATE_BOOK_GENRE_IDS = List.of(2L);

    public static final String NEW_BOOK_TITLE = "NEW_BOOK_TITLE";

    public static final Long NEW_BOOK_AUTHOR_ID = 1L;

    public static final List<Long> NEW_BOOK_GENRES_IDS = List.of(1L, 3L);

    @BeforeEach
    void setUp() {
        if (books == null) {
            books = getBooks();
        }
    }

    @DisplayName("должен загружать книгу по id")
    @Order(1)
    @ParameterizedTest
    @MethodSource("getBookIds")
    void findByIdTest(Long expectedBookId) {
        val expectedBook = books.get(expectedBookId);
        val actualBook = bookService.findById(expectedBook.getId());

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Order(2)
    @Test
    void findAllTest() {
        val actualBooks = bookService.findAll();
        List<Book> expectedBooks = books.values().stream().toList();

        assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks);
    }

    @DisplayName("должен добавлять новые книги")
    @Order(3)
    @Test
    void insertTest() {
        val expectedBook = bookService.insert(NEW_BOOK_TITLE, NEW_BOOK_AUTHOR_ID, NEW_BOOK_GENRES_IDS);
        Optional<Book> actualBook = bookService.findById(expectedBook.getId());

        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.get().getTitle(), NEW_BOOK_TITLE);
        Assertions.assertEquals(actualBook.get().getAuthor().getId(), NEW_BOOK_AUTHOR_ID);
        assertThat(actualBook.get().getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(NEW_BOOK_GENRES_IDS);
    }

    @DisplayName("должен обновлять книги")
    @Order(4)
    @Test
    void updateTest() {
        Book expectedBook = bookService.update(UPDATE_BOOK_ID, UPDATE_BOOK_TITLE, UPDATE_BOOK_AUTHOR_ID, UPDATE_BOOK_GENRE_IDS);
        Optional<Book> actualBook = bookService.findById(UPDATE_BOOK_ID);

        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.get().getTitle(), UPDATE_BOOK_TITLE);
        Assertions.assertEquals(actualBook.get().getAuthor().getId(), UPDATE_BOOK_AUTHOR_ID);
        assertThat(actualBook.get().getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(UPDATE_BOOK_GENRE_IDS);
    }

    @DisplayName("должен удалять книгу по Id")
    @Order(5)
    @Test
    void deleteByIdTest() {
        bookService.deleteById(DELETE_BOOK_ID);
        Optional<Book> deletedBook = bookService.findById(DELETE_BOOK_ID);
        assertThat(deletedBook).isEmpty();
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }

    private static Map<Long, Book> getBooks() {
        List<Long> bookIds =  getBookIds();
        Map<Long, Book> books = new HashMap<>();

        for (Long bookId: bookIds) {
            Long idAuthor =  BOOK_ID_CONTAINS_AUTHOR_ID.get(bookId);
            Author author = Author.builder().id(idAuthor).fullName("Author_" + idAuthor).build();

            List<Genre> genres = BOOK_ID_CONTAINS_GENRE_ID.get(bookId).stream().map(genreId ->
                    Genre.builder().id(genreId).name("Genre_" + genreId).build()).toList();

            Book book = Book.builder().id(bookId).title("BookTitle_" + bookId).author(author).genres(genres).build();

            books.put(bookId, book);
        }

        return books;
    }
}
