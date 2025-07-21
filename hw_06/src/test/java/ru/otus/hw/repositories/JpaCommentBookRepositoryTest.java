package ru.otus.hw.repositories;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Book;
import ru.otus.models.BookComment;
import ru.otus.repositories.BookCommentRepository;
import ru.otus.repositories.JpaBookCommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@DisplayName("Репозиторий на основе Jpa для работы с книгами ")
@DataJpaTest
@Import({JpaBookCommentRepository.class})
public class JpaCommentBookRepositoryTest {
    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private TestEntityManager em;

    public static final Long ID_BOOK_FOR_FIND_COMMENTS = 1L;
    public static final Long ID_BOOK_FOR_SAVE_COMMENT = 3L;

    public static final Long ID_COMMENT_BOOK_FOR_UPDATE = 1L;

    public static final Long ID_COMMENT_BOOK_FOR_DELETE = 1L;

    public static final String NEW_COMMENT_TEXT = "NEW_COMMENT_TEXT";

    @DisplayName("должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getCommentBookIds")
    void findByIdTest(Long id) {
        val expectedCommentBook = em.find(BookComment.class, id);
        val actualCommentBook = bookCommentRepository.findById(id);

        Assertions.assertThat(actualCommentBook).isPresent().get().isEqualTo(expectedCommentBook);
    }

    @DisplayName("должен загружать комментарий по id книги")
    @Test
    void findByBookIdTest() {
        val book = em.find(Book.class, ID_BOOK_FOR_FIND_COMMENTS);
        Hibernate.initialize(book.getComments());
        List<BookComment> expectedBookComments = book.getComments();

        List<BookComment> actualBookComments = bookCommentRepository.findByBookId(book.getId());

        Assertions.assertThat(actualBookComments).containsExactlyInAnyOrderElementsOf(expectedBookComments);
    }

    @DisplayName("должен сохранять комментарии")
    @Test
    void saveToInsertTest() {
        BookComment bookCommentForSave = new BookComment();
        Book book = em.find(Book.class, ID_BOOK_FOR_SAVE_COMMENT);
        em.detach(book);
        bookCommentForSave.setBook(book);
        bookCommentForSave.setText(NEW_COMMENT_TEXT);

        BookComment expectedBookComment = bookCommentRepository.save(bookCommentForSave);

        BookComment actualBookComment = em.find(BookComment.class, expectedBookComment.getId());

        Assertions.assertThat(actualBookComment).isEqualTo(expectedBookComment);
        Assertions.assertThat(actualBookComment.getText()).isEqualTo(NEW_COMMENT_TEXT);
    }

    @DisplayName("должен обновлять комментарии")
    @Test
    @Transactional
    void saveToUpdateTest() {
        BookComment expectedBookComment = em.find(BookComment.class, ID_COMMENT_BOOK_FOR_UPDATE);
        expectedBookComment.setText(NEW_COMMENT_TEXT);
        em.detach(expectedBookComment);

        bookCommentRepository.save(expectedBookComment);

        BookComment actualBookComment = em.find(BookComment.class, ID_COMMENT_BOOK_FOR_UPDATE);

        Assertions.assertThat(actualBookComment).isEqualTo(expectedBookComment);
        Assertions.assertThat(actualBookComment.getText()).isEqualTo(NEW_COMMENT_TEXT);
    }

    @DisplayName("должен удалять комментарии")
    @Test
    void delete() {
        val bookCommentForDelete = em.find(BookComment.class, ID_COMMENT_BOOK_FOR_DELETE);
        em.detach(bookCommentForDelete);

        bookCommentRepository.delete(bookCommentForDelete);

        Optional<BookComment> deletedBookComment = Optional.ofNullable(em.find(BookComment.class,
                ID_COMMENT_BOOK_FOR_DELETE));

        Assertions.assertThat(deletedBookComment).isEmpty();
    }


    static List<Long> getCommentBookIds() {
        return LongStream.range(1, 5).boxed().toList();
    }
}
