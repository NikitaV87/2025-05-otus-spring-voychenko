package ru.otus.hw.repositories;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Genre;
import ru.otus.repositories.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager em;

    private List<Long> dbGenreIds;

    private List<Long> dbBooksIds;

    private static final Long ID_BOOK_UPDATE = 1L;

    private static final Long ID_BOOK_DELETE = 1L;

    private static final Long ID_BOOK_SELECT = 1L;


    @BeforeEach
    void setUp() {
        dbGenreIds = getGenreIds();
        dbBooksIds = getBookIds();
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getBookIds")
    void shouldReturnCorrectBookById(Long expectedBookId) {
        Book expectedBook = em.find(Book.class, expectedBookId);
        Optional<Book> actualBook = bookRepository.findById(expectedBook.getId());

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать книгу по id вместе с полем author, genres")
    @Test
    void shouldReturnCorrectBookByIdWithoutLazyField() {
        Book exceptedBook = em.find(Book.class, ID_BOOK_SELECT);
        Hibernate.initialize(exceptedBook.getAuthor());
        Hibernate.initialize(exceptedBook.getGenres());
        em.detach(exceptedBook);

        Optional<Book> actualBook = bookRepository.findById(ID_BOOK_SELECT);
        assertThat(actualBook).isPresent();
        em.detach(actualBook.get());

        assertThat(actualBook.get()).usingRecursiveComparison().comparingOnlyFields("author").isEqualTo(exceptedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookRepository.findAll();
        List<Book> expectedBooks = dbBooksIds.stream().map(id -> em.find(Book.class, id)).toList();

        assertThat(actualBooks).usingRecursiveComparison().isEqualTo(expectedBooks);
    }

    @DisplayName("должен загружать все книги вместе с полем author")
    @Test
    void shouldReturnCorrectBookAllWithoutLazyField() {
        Book exceptedBook = em.find(Book.class, ID_BOOK_SELECT);
        Hibernate.initialize(exceptedBook.getAuthor());
        em.detach(exceptedBook);

        Optional<Book> actualBook = bookRepository.findById(ID_BOOK_SELECT);
        assertThat(actualBook).isPresent();
        em.detach(actualBook.get());

        assertThat(actualBook.get()).usingRecursiveComparison().comparingOnlyFields("author").isEqualTo(exceptedBook);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        Book newBook = new Book();
        newBook.setAuthor(new Author(null, "NEW_AUTHOR"));
        newBook.setTitle("Title_NewBook");
        newBook.setGenres(List.of(em.find(Genre.class, dbGenreIds.get(1)),
                em.find(Genre.class, dbGenreIds.get(5))));
        Book expectedBook = bookRepository.save(newBook);

        Optional<Book> actualBook = Optional.ofNullable(em.find(Book.class, expectedBook.getId()));

        assertThat(actualBook).isPresent().get().usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        Book expectedBook = em.find(Book.class, ID_BOOK_UPDATE);
        em.detach(expectedBook);

        expectedBook.setTitle("Tittle_new");

        bookRepository.save(expectedBook);
        Optional<Book> actualBook = Optional.ofNullable(em.find(Book.class, ID_BOOK_UPDATE));

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get()).usingRecursiveComparison().comparingOnlyFields("title").isEqualTo(expectedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        Book bookForDelete = em.find(Book.class, ID_BOOK_DELETE);
        assertThat(bookForDelete).isNotNull();
        em.detach(bookForDelete);

        bookRepository.deleteById(ID_BOOK_DELETE);
        Optional<Book> deletedBook = Optional.ofNullable(em.find(Book.class, ID_BOOK_DELETE));

        assertThat(deletedBook).isEmpty();
    }

    private static List<Long> getGenreIds() {
        return LongStream.range(1, 7).boxed().toList();
    }

    private static List<Long> getBookIds() {
        return LongStream.range(1, 4).boxed().toList();
    }
}