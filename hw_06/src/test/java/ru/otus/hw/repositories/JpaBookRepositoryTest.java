package ru.otus.hw.repositories;

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
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.BookComment;
import ru.otus.models.Genre;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.JpaBookRepository;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Long> dbGenreIds;

    private List<Long> dbBooksIds;

    private static final Long ID_BOOK_UPDATE = 1L;

    private static final Long ID_BOOK_DELETE = 1L;

    private static final Long ID_BOOK_SELECT = 1L;

    private static final Long BOOK_SELECT_SIZE_GENRE = 2L;

    private static final Long BOOK_SELECT_SIZE_COMMENT = 3L;


    @BeforeEach
    void setUp() {
        dbGenreIds = getGenreIds();
        dbBooksIds = getBookIds();
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldReturnCorrectBookById(Long expectedBookId) {
        val expectedBook = em.find(Book.class, expectedBookId);
        val actualBook = bookRepository.findById(expectedBook.getId());

        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать книгу по id вместе с полями genres, author и comments")
    @Test
    void shouldReturnCorrectBookByIdWithoutLazyField() {
        val actualBook = bookRepository.findById(ID_BOOK_SELECT);
        assertThat(actualBook).isPresent();

        em.detach(actualBook.get());

        Assertions.assertNotNull(actualBook.get().getAuthor().getFullName());
        Assertions.assertEquals(actualBook.get().getGenres().size(), BOOK_SELECT_SIZE_GENRE);
        Assertions.assertEquals(actualBook.get().getComments().size(), BOOK_SELECT_SIZE_COMMENT);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        val actualBooks = bookRepository.findAll();
        List<Book> expectedBooks = dbBooksIds.stream().map(id -> em.find(Book.class, id)).toList();

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        val newBook = new Book();
        newBook.setAuthor(new Author(null, "NEW_AUTHOR"));
        newBook.setTitle("Title_NewBook");
        newBook.setGenres(List.of(em.find(Genre.class, dbGenreIds.get(1)),
                em.find(Genre.class, dbGenreIds.get(5))));
        newBook.setComments(List.of(BookComment.builder().text("Comment 1").book(newBook).build()));
        val expectedBook = bookRepository.save(newBook);

        Book actualBook = em.find(Book.class, expectedBook.getId());
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        val expectedBook = em.find(Book.class, ID_BOOK_UPDATE);
        String oldTitle = expectedBook.getTitle();
        em.detach(expectedBook);

        expectedBook.setTitle("Tittle_new");

        bookRepository.save(expectedBook);
        val actualBook = em.find(Book.class, ID_BOOK_UPDATE);

        assertThat(actualBook.getTitle()).isNotEqualTo(oldTitle);
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        val bookForDelete = em.find(Book.class, ID_BOOK_DELETE);
        assertThat(bookForDelete).isNotNull();
        em.detach(bookForDelete);

        bookRepository.deleteById(ID_BOOK_DELETE);
        val deletedBook = em.find(Book.class, ID_BOOK_DELETE);

        assertThat(deletedBook).isNull();
    }

    private static List<Long> getGenreIds() {
        return LongStream.range(1, 7).boxed().toList();
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }
}