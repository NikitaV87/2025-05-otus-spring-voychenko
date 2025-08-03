package ru.otus.hw.repositories;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.models.Book;
import ru.otus.models.Comment;
import ru.otus.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

@DisplayName("Репозиторий на основе Jpa для работы с комментарием")
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

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
        val expectedCommentBook = em.find(Comment.class, id);
        val actualCommentBook = commentRepository.findById(id);

        Assertions.assertThat(actualCommentBook).isPresent().get()
                .usingRecursiveComparison()
                .isEqualTo(expectedCommentBook);
    }

    @DisplayName("должен загружать комментарий по id книги")
    @Test
    void findByBookIdTest() {
        val book = em.find(Book.class, ID_BOOK_FOR_FIND_COMMENTS);
        List<Comment> expectedComments = book.getComments();

        List<Comment> actualComments = commentRepository.findByBookId(ID_BOOK_FOR_FIND_COMMENTS);

        Assertions.assertThat(actualComments).usingRecursiveComparison().isEqualTo(expectedComments);
    }

    @DisplayName("должен сохранять комментарии")
    @Test
    void saveToInsertTest() {
        Comment commentForSave = new Comment();
        Book book = em.find(Book.class, ID_BOOK_FOR_SAVE_COMMENT);
        em.detach(book);

        commentForSave.setBook(book);
        commentForSave.setText(NEW_COMMENT_TEXT);

        Comment expectedComment = commentRepository.save(commentForSave);

        Optional<Comment> actualBookComment = Optional.ofNullable(em.find(Comment.class, expectedComment.getId()));

        Assertions.assertThat(actualBookComment).isPresent();
        Assertions.assertThat(actualBookComment.get().getText()).isEqualTo(NEW_COMMENT_TEXT);
        Assertions.assertThat(actualBookComment.get().getBook().getId()).isEqualTo(ID_BOOK_FOR_SAVE_COMMENT);
    }

    @DisplayName("должен обновлять комментарии")
    @Test
    @Transactional
    void saveToUpdateTest() {
        Comment expectedComment = em.find(Comment.class, ID_COMMENT_BOOK_FOR_UPDATE);
        expectedComment.setText(NEW_COMMENT_TEXT);
        em.detach(expectedComment);

        commentRepository.save(expectedComment);

        Comment actualComment = em.find(Comment.class, ID_COMMENT_BOOK_FOR_UPDATE);

        Assertions.assertThat(actualComment).isEqualTo(expectedComment);
        Assertions.assertThat(actualComment.getText()).isEqualTo(NEW_COMMENT_TEXT);
    }

    @DisplayName("должен удалять комментарии")
    @Test
    void deleteTest() {
        val bookCommentForDelete = em.find(Comment.class, ID_COMMENT_BOOK_FOR_DELETE);
        em.detach(bookCommentForDelete);

        commentRepository.delete(bookCommentForDelete);

        Optional<Comment> deletedBookComment = Optional.ofNullable(em.find(Comment.class,
                ID_COMMENT_BOOK_FOR_DELETE));

        Assertions.assertThat(deletedBookComment).isEmpty();
    }


    static List<Long> getCommentBookIds() {
        return LongStream.range(1, 5).boxed().toList();
    }
}
