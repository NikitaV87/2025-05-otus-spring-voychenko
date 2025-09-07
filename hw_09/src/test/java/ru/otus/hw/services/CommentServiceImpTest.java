package ru.otus.hw.services;

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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.dto.GenreDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.services.CommentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест сервиса CommentServiceImpl")
@Transactional(propagation = Propagation.NEVER)
public class CommentServiceImpTest {

    @Autowired
    CommentService commentService;

    private Map<Long, CommentDto> commentsById;

    private Map<Long, List<CommentDto>> commentsByBookId;

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
        CommentDto exceptComment = commentsById.get(expectedBookId);
        CommentDto actualComment = commentService.findById(expectedBookId);

        assertThat(actualComment).usingRecursiveComparison().comparingOnlyFields("id", "text", "book.id").isEqualTo(exceptComment);
    }

    @DisplayName("должен загружать комментарии по id книги")
    @Test
    @Order(2)
    void findByBookIdTest() {
        List<CommentDto> exceptComments = commentsByBookId.get(BOOK_ID_WITH_COMMENTS);
        List<CommentDto> actualComments = commentService.findByBookId(BOOK_ID_WITH_COMMENTS);

        assertThat(actualComments).usingRecursiveComparison().comparingOnlyFields("id", "text", "book.id").isEqualTo(exceptComments);
    }

    @DisplayName("должен вставлять новые комментарии")
    @Test
    @Order(3)
    void insertTest() {
        CommentDto comment = commentService.insert(new CommentCreateDto(FOR_NEW_COMMENT_TEXT, FOR_NEW_COMMENT_BOOK_ID));
        Assertions.assertNotNull(comment.getId());
        CommentDto savedComment = commentService.findById(comment.getId());

        Assertions.assertEquals(savedComment.getText(), FOR_NEW_COMMENT_TEXT);
        Assertions.assertEquals(savedComment.getBook().getId(), FOR_NEW_COMMENT_BOOK_ID);
    }

    @DisplayName("должен обновлять комментарии")
    @Test
    @Order(4)
    void updateTest() {
        CommentDto comment = commentService.update(new CommentUpdateDto(FOR_UPD_COMMENT_ID, FOR_UPD_COMMENT_TEXT,
                BOOK_ID_WITH_COMMENTS));
        CommentDto updatedComment = commentService.findById(FOR_UPD_COMMENT_ID);

        Assertions.assertEquals(updatedComment.getText(), FOR_UPD_COMMENT_TEXT);
        Assertions.assertEquals(updatedComment.getBook().getId(), comment.getBook().getId());
    }

    @DisplayName("должен удалять комментарии по ID")
    @Test
    @Order(4)
    void deleteTest() {
        commentService.deleteById(FOR_DEL_COMMENT_ID);
        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.findById(FOR_DEL_COMMENT_ID));
    }

    private static Map<Long, CommentDto> getComments() {
        List<CommentDto> allComment = new ArrayList<>();

        for (Long bookId: COMMENT_ID_BY_BOOK_ID.keySet()) {
            Long idAuthor =  BOOK_ID_CONTAINS_AUTHOR_ID.get(bookId);
            AuthorDto author = new AuthorDto(idAuthor, "Author_" + idAuthor);
            List<GenreDto> genres = BOOK_ID_CONTAINS_GENRE_ID.get(bookId).stream().map(genreId ->
                    new GenreDto(genreId, "Genre_" + genreId)).toList();
            BookDto book = BookDto.builder().id(bookId).title("BookTitle_" + bookId).author(author).genres(genres).build();

            List<CommentDto> bookComments = COMMENT_ID_BY_BOOK_ID.get(bookId).stream().map(idBookComment ->
                    CommentDto.builder()
                            .id(idBookComment).book(BookDto.builder().id(book.getId()).build()).text("text_" + idBookComment).build()).toList();

            allComment.addAll(bookComments);
        }


        return allComment.stream().collect(Collectors.toMap(CommentDto::getId, Function.identity()));
    }

    static private Map<Long, List<CommentDto>> getGroupByBookId(List<CommentDto> comments) {
        return comments.stream()
                .collect(Collectors.groupingBy( c -> c.getBook().getId()));
    }

    static private List<Long> getCommentIds() {
        return LongStream.range(1, 5).boxed().toList();
    }
}
