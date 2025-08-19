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
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.services.BookService;
import ru.otus.services.BookServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест сервиса BookServiceImpl")
@Import({BookServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;

    private Map<String, Book> books;

    private static final Map<String, String> BOOK_ID_CONTAINS_AUTHOR_ID = Map.of("1","1", "2", "2", "3", "3");

    private static final Map<String, List<String>> BOOK_ID_CONTAINS_GENRE_ID = Map.of("1", List.of("1", "2"),
            "2", List.of("3", "4"), "3", List.of("5", "6"));

    public static final String DELETE_BOOK_ID = "3";

    public static final String UPDATE_BOOK_ID = "1";

    public static final String UPDATE_BOOK_TITLE = "UPDATE_BOOK_TITLE";

    public static final String UPDATE_BOOK_AUTHOR_ID = "2";

    public static final List<String> UPDATE_BOOK_GENRE_IDS = List.of("2");

    public static final String NEW_BOOK_TITLE = "NEW_BOOK_TITLE";

    public static final String NEW_BOOK_AUTHOR_ID = "1";

    public static final List<String> NEW_BOOK_GENRES_IDS = List.of("1", "3");

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
    void findByIdTest(String expectedBookId) {
        val exceptBook = books.get(expectedBookId);
        val actualBook = bookService.findById(expectedBookId);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get()).usingRecursiveComparison().isEqualTo(exceptBook);
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

        assertThat(actualBook).isPresent();

        Assertions.assertEquals(actualBook.get().getTitle(), NEW_BOOK_TITLE);
        Assertions.assertEquals(actualBook.get().getAuthor().getId(), NEW_BOOK_AUTHOR_ID);
        assertThat(actualBook.get().getGenres().stream().map(Genre::getId).toList())
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

    private static List<String> getBookIds() {
        return LongStream.range(1, 4).boxed().map(Object::toString).toList();
    }

    private static Map<String, Book> getBooks() {
        List<String> bookIds =  getBookIds();
        Map<String, Book> books = new HashMap<>();

        for (String bookId: bookIds) {
            String idAuthor =  BOOK_ID_CONTAINS_AUTHOR_ID.get(bookId);
            Author author = Author.builder().id(idAuthor).fullName("Author_" + idAuthor).build();

            List<Genre> genres = BOOK_ID_CONTAINS_GENRE_ID.get(bookId).stream().map(genreId ->
                    Genre.builder().id(genreId).name("Genre_" + genreId).build()).toList();

            Book book = Book.builder().id(bookId).title("BookTitle_" + bookId).author(author).genres(genres).build();

            books.put(bookId, book);
        }

        return books;
    }
}
