package ru.otus.hw.conroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.CommentController;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.dto.GenreDto;
import ru.otus.services.BookService;
import ru.otus.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    public static final List<BookDto> BOOKS = List.of(BookDto.builder()
                    .id(1L)
                    .title("Book_1")
                    .author(new AuthorDto(1L, "Author_1"))
                    .genres(List.of(new GenreDto(1L, "Genre_1"))).build(),
            BookDto.builder()
                    .id(1L)
                    .title("Book_2")
                    .author(new AuthorDto(1L, "Author_2"))
                    .genres(List.of(new GenreDto(1L, "Genre_2"))).build());

    public static final List<CommentDto> COMMENTS = List.of(
            CommentDto.builder().id(1L).text("COMMENT_1").book(BOOKS.get(0)).build(),
            CommentDto.builder().id(2L).text("COMMENT_2").book(BOOKS.get(0)).build()
    );

    public static final Long BOOK_ID = 1L;

    public static final Long COMMENT_ID = 1L;

    public static final Integer COMMENT_INDEX_IN_LIST = 0;

    public static final Integer BOOK_INDEX_IN_LIST = 0;

    public static final String COMMENT_TEXT = "Comment text";

    @DisplayName("Тест выдачи книг для комментариев")
    @Test
    void getAllBooksTest() throws Exception {
        when(bookService.findAll()).thenReturn(BOOKS);

        mockMvc.perform(get("/comment/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/book"))
                .andExpect(model().attribute("books", BOOKS));
    }

    @DisplayName("Тест выдачи комментариев по идентификатору книги")
    @Test
    void getCommentsByBookTest() throws Exception {
        when(commentService.findByBookId(BOOK_ID)).thenReturn(COMMENTS);

        mockMvc.perform(get("/comment/book/%d".formatted(BOOK_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/comments"))
                .andExpect(model().attribute("comments", COMMENTS))
                .andExpect(model().attribute("bookId", BOOK_ID));
    }

    @DisplayName("Тест показа формы для обновления комментария")
    @Test
    void getUpdateCommentTest() throws Exception {
        when(commentService.findById(COMMENT_ID)).thenReturn(COMMENTS.get(COMMENT_INDEX_IN_LIST));


        mockMvc.perform(get("/comment/%d".formatted(COMMENT_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/formUpd"))
                .andExpect(model().attribute("comment", CommentUpdateDto.builder()
                        .bookId(COMMENTS.get(COMMENT_INDEX_IN_LIST).getBook().getId())
                        .id(COMMENTS.get(COMMENT_INDEX_IN_LIST).getId())
                        .text(COMMENTS.get(COMMENT_INDEX_IN_LIST).getText()).build()))
                .andExpect(model().attribute("books",  List.of(BOOKS.get(0))));
    }

    @DisplayName("Тест обновления комментария")
    @Test
    void postUpdateCommentTest() throws Exception {
        when(commentService.findById(COMMENT_ID)).thenReturn(COMMENTS.get(COMMENT_INDEX_IN_LIST));

        mockMvc.perform(post("/comment/%d".formatted(COMMENT_ID))
                        .param("id", String.valueOf(COMMENT_ID))
                        .param("text", COMMENT_TEXT)
                        .param("bookId", String.valueOf(BOOK_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comment/book/%d".formatted(BOOK_ID)))
                .andExpect(model().hasNoErrors());

        verify(commentService).update(new CommentUpdateDto(COMMENT_ID, COMMENT_TEXT, BOOK_ID));
    }

    @Test
    @DisplayName("Тест показа формы для создания комментария")
    void getNewCommentTest() throws Exception {
        when(bookService.findById(BOOK_ID)).thenReturn(BOOKS.get(BOOK_INDEX_IN_LIST));

        mockMvc.perform(get("/comment/new/book/%d".formatted(COMMENT_ID))
                        .param("books", String.valueOf(BOOK_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/formCreate"))
                .andExpect(model().attribute("comment",
                        CommentCreateDto.builder().bookId(BOOKS.get(0).getId()).build()))
                .andExpect(model().attribute("books", List.of(BOOKS.get(BOOK_INDEX_IN_LIST))))
                .andExpect(model().hasNoErrors());
    }

    @Test
    @DisplayName("Тест создания комментария")
    void postNewCommentTest() throws Exception {
        when(bookService.findById(BOOK_ID)).thenReturn(BOOKS.get(BOOK_INDEX_IN_LIST));

        mockMvc.perform(post("/comment/new/book/%d".formatted(BOOK_ID))
                        .param("id", String.valueOf(COMMENT_ID))
                        .param("text", COMMENT_TEXT)
                        .param("bookId", String.valueOf(BOOK_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comment/book/%d".formatted(BOOK_ID)))
                .andExpect(model().hasNoErrors());

        verify(commentService).insert(new CommentCreateDto(COMMENT_TEXT, BOOK_ID));
    }

    @Test
    @DisplayName("Тест удаления комментария")
    void deleteCommentByIdTest() throws Exception {
        when(commentService.findById(COMMENT_ID)).thenReturn(COMMENTS.get(COMMENT_INDEX_IN_LIST));

        mockMvc.perform(post("/comment/%d/delete".formatted(COMMENT_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/comment/book/%d".formatted(BOOK_ID)))
                .andExpect(model().hasNoErrors());

        verify(commentService).deleteById(COMMENT_ID);
    }
}
