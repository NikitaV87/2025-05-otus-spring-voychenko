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
import ru.otus.models.BookComment;
import ru.otus.models.Genre;
import ru.otus.repositories.JpaAuthorRepository;
import ru.otus.repositories.JpaBookCommentRepository;
import ru.otus.repositories.JpaBookRepository;
import ru.otus.repositories.JpaGenreRepository;
import ru.otus.services.BookService;
import ru.otus.services.BookServiceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест сервиса BookServiceImpl")
@Import({JpaBookRepository.class, JpaAuthorRepository.class,
        JpaGenreRepository.class, JpaBookCommentRepository.class,
        BookServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class BookServiceImplTest {
    @Autowired
    private BookService bookService;

    private Map<Long, Book> books;

    private static final Map<Long, Long> BOOK_ID_CONTAINS_AUTHOR_ID = Map.of(1L,1L, 2L, 2L, 3L, 3L);

    private static final Map<Long, List<Long>> BOOK_ID_CONTAINS_BOOK_COMMENT_ID = Map.of(1L, List.of(1L, 2L, 3L),
            2L, List.of(4L));

    private static final Map<Long, List<Long>> BOOK_ID_CONTAINS_GENRE_ID = Map.of(1L, List.of(1L, 2L),
            2L, List.of(3L, 4L), 3L, List.of(5L, 6L));

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
        if (books == null) {
            books = getBooks();
        }
    }

    @DisplayName("должен загружать книгу по id")
    @Order(1)
    @ParameterizedTest
    @MethodSource("getBookIds")
    void findByIdTest(Long expectedBookId) {
        val exceptBook = books.get(expectedBookId);
        val actualBook = bookService.findById(expectedBookId);

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(exceptBook);
        assertThat(actualBook.get().getTitle()).isEqualTo(exceptBook.getTitle());
        assertThat(actualBook.get().getAuthor()).isEqualTo(exceptBook.getAuthor());
        assertThat(actualBook.get().getGenres()).containsExactlyInAnyOrderElementsOf(exceptBook.getGenres());
        assertThat(actualBook.get().getComments()).containsExactlyInAnyOrderElementsOf(exceptBook.getComments());
    }

    @DisplayName("должен загружать список всех книг")
    @Order(2)
    @Test
    void findAllTest() {
        val actualBooks = bookService.findAll();
        List<Book> expectedBooks = books.values().stream().toList();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
        for (Book actualBook : actualBooks) {
            Long bookId = actualBook.getId();
            assertThat(actualBook.getTitle()).isEqualTo(books.get(bookId).getTitle());
            assertThat(actualBook.getAuthor()).isEqualTo(books.get(bookId).getAuthor());
            assertThat(actualBook.getGenres()).containsExactlyInAnyOrderElementsOf(books.get(bookId).getGenres());
            assertThat(actualBook.getComments()).containsExactlyInAnyOrderElementsOf(books.get(bookId).getComments());
        }
    }

    @DisplayName("должен добавлять новые книги")
    @Order(3)
    @Test
    void insertTest() {
        val expectedBook = bookService.insert(NEW_BOOK_TITLE, NEW_BOOK_AUTHOR_ID, NEW_BOOK_GENRES_IDS, NEW_BOOK_COMMENTS);
        Optional<Book> actualBook = bookService.findById(expectedBook.getId());

        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.get().getTitle(), NEW_BOOK_TITLE);
        Assertions.assertEquals(actualBook.get().getAuthor().getId(), NEW_BOOK_AUTHOR_ID);
        assertThat(actualBook.get().getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(NEW_BOOK_GENRES_IDS);
        assertThat(actualBook.get().getComments().stream().map(BookComment::getText))
                .containsExactlyInAnyOrderElementsOf(NEW_BOOK_COMMENTS);
    }

    @DisplayName("должен обновлять книги")
    @Order(4)
    @Test
    void updateTest() {
        Book expectedBook = bookService.update(UPDATE_BOOK_ID, UPDATE_BOOK_TITLE, UPDATE_BOOK_AUTHOR_ID, UPDATE_BOOK_GENRE_IDS, UPDATE_BOOK_COMMENTS);
        Optional<Book> actualBook = bookService.findById(UPDATE_BOOK_ID);

        assertThat(actualBook).isPresent().get().isEqualTo(expectedBook);
        Assertions.assertEquals(actualBook.get().getTitle(), UPDATE_BOOK_TITLE);
        Assertions.assertEquals(actualBook.get().getAuthor().getId(), UPDATE_BOOK_AUTHOR_ID);
        assertThat(actualBook.get().getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
                .containsExactlyInAnyOrderElementsOf(UPDATE_BOOK_GENRE_IDS);
        assertThat(actualBook.get().getComments().stream().map(BookComment::getText))
                .containsExactlyInAnyOrderElementsOf(UPDATE_BOOK_COMMENTS);
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
    private static List<Long> getBookAuthorIds() {

        return LongStream.range(1, 4).boxed().toList();
    }

    private static Map<Long, Book> getBooks() {
        List<Long> bookIds =  getBookIds();
        Map<Long, Book> books = new HashMap<>();

        for (Long bookId: bookIds) {
            Long idAuthor =  BOOK_ID_CONTAINS_AUTHOR_ID.get(bookId);
            Author author = Author.builder().id(idAuthor).fullName("Author_" + idAuthor).build();

            Set<Genre> genres = BOOK_ID_CONTAINS_GENRE_ID.get(bookId).stream().map(genreId ->
                    Genre.builder().id(genreId).name("Genre_" + genreId).build()).collect(Collectors.toSet());

            Book book = Book.builder().id(bookId).title("BookTitle_" + bookId).author(author).genres(genres).build();

            List<Long> bookCommentIds = BOOK_ID_CONTAINS_BOOK_COMMENT_ID.get(bookId);
            List<BookComment> comments;

            if (bookCommentIds != null) {
                comments = bookCommentIds.stream().map(idBookComment -> {
                    return BookComment.builder()
                            .id(idBookComment).book(book).text("text_" + idBookComment).build();
                }).toList();
            } else {
                comments = Collections.emptyList();
            }

            book.setComments(comments);

            books.put(bookId, book);
        }

        return books;
    }
}
