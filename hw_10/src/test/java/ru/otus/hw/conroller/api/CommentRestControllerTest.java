package ru.otus.hw.conroller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.rest.CommentRestController;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.dto.GenreDto;
import ru.otus.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {CommentRestController.class})
public class CommentRestControllerTest {
    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper mapper;
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

    public static final int INDEX_COMMENT = 0;

    public static final Long BOOK_ID = 1L;

    public static final Long COMMENT_ID = 1L;

    @DisplayName("Тест выдачи комментариев по id комментария")
    @Test
    void getCommentsByIdTest() throws Exception {
        when(commentService.findById(COMMENT_ID)).thenReturn(COMMENTS.get(INDEX_COMMENT));

        mockMvc.perform(get("/api/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COMMENTS.get(INDEX_COMMENT))));

        verify(commentService).findById(COMMENT_ID);
    }

    @DisplayName("Тест выдачи комментариев по id книги")
    @Test
    void getCommentsByBookIdTest() throws Exception {
        when(commentService.findByBookId(BOOK_ID)).thenReturn(COMMENTS);

        mockMvc.perform(get("/api/comment/book/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COMMENTS)));

        verify(commentService).findByBookId(BOOK_ID);
    }

    @DisplayName("Тест обновления комментариев")
    @Test
    void patchCommentTest() throws Exception {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(COMMENTS.get(INDEX_COMMENT).getId())
                .text(COMMENTS.get(INDEX_COMMENT).getText())
                .bookId(COMMENTS.get(INDEX_COMMENT).getBook().getId()).build();

        when(commentService.update(commentUpdateDto)).thenReturn(COMMENTS.get(INDEX_COMMENT));

        mockMvc.perform(patch("/api/comment")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COMMENTS.get(INDEX_COMMENT))));

        verify(commentService).update(commentUpdateDto);
    }

    @DisplayName("Тест создания комментариев")
    @Test
    void postCommentTest() throws Exception {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text(COMMENTS.get(INDEX_COMMENT).getText())
                .bookId(COMMENTS.get(INDEX_COMMENT).getBook().getId()).build();

        when(commentService.insert(commentCreateDto)).thenReturn(COMMENTS.get(INDEX_COMMENT));

        mockMvc.perform(post("/api/comment")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(COMMENTS.get(INDEX_COMMENT))));

        verify(commentService).insert(commentCreateDto);
    }

    @DisplayName("Тест удаления комментариев")
    @Test
    void deleteCommentByIdTest() throws Exception {
        mockMvc.perform(delete("/api/comment/1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(commentService).deleteById(1L);
    }
}
