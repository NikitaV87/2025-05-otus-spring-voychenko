package ru.otus.hw.conroller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.BookController;
import ru.otus.controller.CommentController;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.GenreDto;
import ru.otus.exceptions.EntityNotFoundException;
import ru.otus.services.AuthorService;
import ru.otus.services.BookService;
import ru.otus.services.CommentService;
import ru.otus.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {BookController.class, CommentController.class})
public class ExceptionAdviceControllerTest {

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

    @BeforeEach
    public void init() {
        when(authorService.findAll()).thenReturn(List.of(AuthorDto.builder().id(1L).fullName("Author_1").build()));
        when(genreService.findAll()).thenReturn(List.of(GenreDto.builder().id(1L).name("Genre_1").build()));
    }

    @Test
    public void getNotHaveBook404() throws Exception {
        when(bookService.findById(1)).thenReturn(null);

        mockMvc.perform(get("/book/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error/message"));
    }

    @Test
    public void postNotHaveBook404() throws Exception {
        when(bookService.update(any()))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/book/1").param("title", "title")
                    .param("authorId", String.valueOf(1L))
                    .param("genreIds", String.valueOf(2L)))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error/message"));
    }

    @Test
    public void getNotHaveComment404() throws Exception {
        when(commentService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/comment/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error/message"));
    }

    @Test
    public void postNotHaveComment404() throws Exception {
        when(commentService.findById(1L)).thenReturn(null);

        mockMvc.perform(post("/comment/1")
                        .param("id", "1")
                        .param("text", "text comment")
                        .param("bookId", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("error/message"));
    }

    @Test
    public void testException500() throws Exception {
        when(bookService.update(any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/book/1").param("title", "title")
                        .param("authorId", String.valueOf(1L))
                        .param("genreIds", String.valueOf(2L)))
                .andExpect(status().is5xxServerError())
                .andExpect(view().name("error/message"));
    }
}
