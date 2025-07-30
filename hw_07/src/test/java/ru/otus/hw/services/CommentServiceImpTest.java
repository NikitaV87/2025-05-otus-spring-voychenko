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
import ru.otus.hw.TestUtils;
import ru.otus.models.Author;
import ru.otus.models.Book;
import ru.otus.models.Comment;
import ru.otus.models.Genre;
import ru.otus.services.BookServiceImpl;
import ru.otus.services.CommentService;
import ru.otus.services.CommentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест сервиса CommentServiceImpl")
@Import({BookServiceImpl.class,
        CommentServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImpTest {

    @Autowired
    CommentService commentService;

    private Map<Long, Comment> commentsById;

    private Map<Long, List<Comment>> commentsByBookId;

    public static final Long BOOK_ID_WITH_COMMENTS = 1L;

    public static final String FOR_NEW_COMMENT_TEXT = "NEW_TEXT";

    public static final Long FOR_NEW_COMMENT_BOOK_ID = 1L;

    public static final String FOR_UPD_COMMENT_TEXT = "UPD_TEXT";

    public static final Long FOR_UPD_COMMENT_ID = 1L;

    public static final Long FOR_DEL_COMMENT_ID = 2L;

    private static final Map<Long, Long> BOOK_ID_CONTAINS_AUTHOR_ID = Map.of(1L,1L, 2L, 2L, 3L, 3L);

    private static final Map<Long, List<Long>> COMMENT_ID_BY_BOOK_ID = Map.of(1L, List.of(1L, 2L, 3L),
            2L, List.of(4L));

    private static final Map<Long, List<Long>> BOOK_ID_CONTAINS_GENRE_ID = Map.of(1L, List.of(1L, 2L),
            2L, List.of(3L, 4L), 3L, List.of(5L, 6L));

    @BeforeEach
    void setUp() {
        if (commentsById == null) {
            commentsById = getComments();
        }

        if (commentsByBookId == null) {
            commentsByBookId = getGroupByBookId(new ArrayList<>(commentsById.values()));
        }
    }

    @DisplayName("должен загружать комментарии по id")
    @Order(1)
    @ParameterizedTest
    @MethodSource("getCommentIds")
    void findByIdTest(Long expectedBookId) {
        val exceptComment = commentsById.get(expectedBookId);
        val actualComment = commentService.findById(expectedBookId);

        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(exceptComment);
        TestUtils.equalComment(actualComment.get(), exceptComment);
        TestUtils.equalBook(actualComment.get().getBook(), exceptComment.getBook());
    }

    @DisplayName("должен загружать комментарии по id книги")
    @Test
    @Order(2)
    void findByBookIdTest() {
        val exceptComments = commentsByBookId.get(BOOK_ID_WITH_COMMENTS);
        val actualComments = commentService.findByBookId(BOOK_ID_WITH_COMMENTS);

        assertThat(actualComments).containsExactlyInAnyOrderElementsOf(exceptComments);
        TestUtils.equalComments(actualComments, exceptComments);
    }

    @DisplayName("должен вставлять новые комментарии")
    @Test
    @Order(3)
    void insertTest() {
        val comment = commentService.insert(FOR_NEW_COMMENT_TEXT, FOR_NEW_COMMENT_BOOK_ID);
        Assertions.assertNotNull(comment.getId());
        val savedComment = commentService.findById(comment.getId());

        assertThat(savedComment).isPresent();
        Assertions.assertEquals(savedComment.get().getText(), FOR_NEW_COMMENT_TEXT);
        Assertions.assertEquals(savedComment.get().getBook().getId(), FOR_NEW_COMMENT_BOOK_ID);
    }

    @DisplayName("должен обновлять комментарии")
    @Test
    @Order(4)
    void updateTest() {
        val comment = commentService.update(FOR_UPD_COMMENT_ID, FOR_UPD_COMMENT_TEXT);
        val updatedComment = commentService.findById(FOR_UPD_COMMENT_ID);

        assertThat(updatedComment).isPresent();
        Assertions.assertEquals(updatedComment.get().getText(), FOR_UPD_COMMENT_TEXT);
        Assertions.assertEquals(updatedComment.get().getBook().getId(), comment.getBook().getId());
    }

    @DisplayName("должен удалять комментарии по ID")
    @Test
    @Order(4)
    void deleteTest() {
        commentService.deleteById(FOR_DEL_COMMENT_ID);
        Optional<Comment> deletedComment = commentService.findById(FOR_DEL_COMMENT_ID);

        assertThat(deletedComment).isEmpty();
    }


    private static Map<Long, Comment> getComments() {
        List<Comment> allComment = new ArrayList<>();

        for (Long bookId: COMMENT_ID_BY_BOOK_ID.keySet()) {
            Long idAuthor =  BOOK_ID_CONTAINS_AUTHOR_ID.get(bookId);
            Author author = Author.builder().id(idAuthor).fullName("Author_" + idAuthor).build();
            List<Genre> genres = BOOK_ID_CONTAINS_GENRE_ID.get(bookId).stream().map(genreId ->
                    Genre.builder().id(genreId).name("Genre_" + genreId).build()).toList();
            Book book = Book.builder().id(bookId).title("BookTitle_" + bookId).author(author).genres(genres).build();

            List<Comment> bookComments = COMMENT_ID_BY_BOOK_ID.get(bookId).stream().map(idBookComment ->
                Comment.builder()
                        .id(idBookComment).book(book).text("text_" + idBookComment).build()).toList();

            allComment.addAll(bookComments);
        }


        return allComment.stream().collect(Collectors.toMap(Comment::getId, Function.identity()));
    }

    static private Map<Long, List<Comment>> getGroupByBookId(List<Comment> comments) {
        return comments.stream()
                .collect(Collectors.groupingBy(c -> c.getBook().getId()));
    }

    static private List<Long> getCommentIds() {
        return LongStream.range(1, 5).boxed().toList();
    }
}
