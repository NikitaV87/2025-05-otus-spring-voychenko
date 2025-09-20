package ru.otus.hw.conroller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.otus.controller.rest.BookRestController;
import ru.otus.controller.rest.CommentRestController;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.services.AuthorService;
import ru.otus.services.BookService;
import ru.otus.services.CommentService;
import ru.otus.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BookRestController.class, CommentRestController.class})
public class ExceptionAdviceRestControllerTest {

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public static final Long BOOK_ID = 1L;

    public static final Long BOOK_AUTHOR_ID = 2L;

    public static final Long BOOK_GENRE_ID = 2L;

    public static final String BOOK_TITLE = "Book title";

    @Test
    public void getNotHaveBook404() throws Exception {
        when(bookService.findById(1L)).thenThrow(new EntityNotFoundException("test"));

        mockMvc.perform(get("/api/book/1"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    public void postNotHaveBook404() throws Exception {
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder().id(1L).title("title").authorId(1L)
                .genreIds(List.of(1L)).build();
        when(bookService.update(any()))
                .thenThrow(new EntityNotFoundException("test"));

        mockMvc.perform(patch("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    public void getNotHaveComment404() throws Exception {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder().text("text").bookId(1L).id(1L).build();
        when(commentService.update(commentUpdateDto)).thenThrow(new EntityNotFoundException("test"));

        mockMvc.perform(patch("/api/comment")
                         .contentType(APPLICATION_JSON)
                         .content(mapper.writeValueAsString(commentUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @Test
    public void testException500() throws Exception {
        when(bookService.update(any())).thenThrow(new RuntimeException("test"));

        mockMvc.perform(post("/book/1").param("title", "title")
                        .param("authorId", String.valueOf(1L))
                        .param("genreIds", String.valueOf(2L)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("message").exists());
    }

    @DisplayName("Тест валидации при создание новой книги")
    @Test
    void postNewBookValidationTest() throws Exception {
        BookCreateDto bookCreateDto = BookCreateDto.builder()
                .title(BOOK_TITLE)
                .authorId(null)
                .genreIds(List.of(BOOK_GENRE_ID)).build();

        mockMvc.perform(post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("authorId").exists());

        verify(bookService, never()).insert(any());

        bookCreateDto = BookCreateDto.builder()
                .title(null)
                .authorId(BOOK_AUTHOR_ID)
                .genreIds(List.of(BOOK_GENRE_ID)).build();

        mockMvc.perform(post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("title").exists());

        verify(bookService, never()).insert(any());

        bookCreateDto = BookCreateDto.builder()
                .title(BOOK_TITLE)
                .authorId(BOOK_AUTHOR_ID)
                .genreIds(null).build();

        mockMvc.perform(post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("genreIds").exists());

        verify(bookService, never()).insert(any());
    }

    @DisplayName("Тест валидации при обновлении книги")
    @Test
    void postUpdateBookValidationTest() throws Exception {
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(BOOK_ID)
                .title(BOOK_TITLE)
                .authorId(null)
                .genreIds(List.of(BOOK_GENRE_ID)).build();

        mockMvc.perform(patch("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("authorId").exists());

        verify(bookService, never()).update(bookUpdateDto);

        bookUpdateDto = BookUpdateDto.builder()
                .id(BOOK_ID)
                .title(null)
                .authorId(BOOK_AUTHOR_ID)
                .genreIds(List.of(BOOK_GENRE_ID)).build();

        mockMvc.perform(patch("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("title").exists());

        verify(bookService, never()).update(bookUpdateDto);

        bookUpdateDto = BookUpdateDto.builder()
                .id(BOOK_ID)
                .title(BOOK_TITLE)
                .authorId(BOOK_AUTHOR_ID)
                .genreIds(null).build();

        mockMvc.perform(patch("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("genreIds").exists());

        verify(bookService, never()).update(bookUpdateDto);
    }
}
