package ru.otus.hw.conroller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.rest.BookRestController;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.dto.GenreDto;
import ru.otus.mapper.AuthorMapperImpl;
import ru.otus.mapper.BookMapper;
import ru.otus.mapper.BookMapperImpl;
import ru.otus.mapper.GenreMapperImpl;
import ru.otus.services.BookService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тест REST API запросов для книг")
@WebMvcTest(controllers = {BookRestController.class,
        BookMapperImpl.class, AuthorMapperImpl.class, GenreMapperImpl.class})
class BookRestControllerTest {
    @MockitoBean
    private BookService bookService;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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

    public static final Integer INDEX_BOOK = 0;

    @DisplayName("Тест выдачи книг")
    @Test
    void getAllBooksTest() throws Exception {
        when(bookService.findAll()).thenReturn(BOOKS);

        mockMvc.perform(get("/api/book" ))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BOOKS)));
    }

    @DisplayName("Тест выдачи книг по id")
    @Test
    void getBookByIdTest() throws Exception {
        BookDto bookDto = BOOKS.get(INDEX_BOOK);
        when(bookService.findById(bookDto.getId())).thenReturn(bookDto);

        mockMvc.perform(get("/api/book/" + bookDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));
    }

    @DisplayName("Тест создание новой книги")
    @Test
    void postBookTest() throws Exception {
        BookDto bookDto = BOOKS.get(INDEX_BOOK);
        BookCreateDto bookCreateDto = BookCreateDto.builder()
                .title(bookDto.getTitle())
                .authorId(bookDto.getAuthor().getId())
                .genreIds(bookDto.getGenres().stream().map(GenreDto::getId).toList()).build();
        when(bookService.insert(bookCreateDto)).thenReturn(bookDto);

        mockMvc.perform(post("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));

        verify(bookService).insert(new BookCreateDto(bookCreateDto.getTitle(),
                                    bookCreateDto.getAuthorId(), bookCreateDto.getGenreIds()));
    }

    @DisplayName("Тест для обновления книги")
    @Test
    void patchBookTest() throws Exception {
        BookDto bookDto = BOOKS.get(INDEX_BOOK);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(bookDto.getId())
                .title(bookDto.getTitle())
                .authorId(bookDto.getAuthor().getId())
                .genreIds(bookDto.getGenres().stream().map(GenreDto::getId).toList()).build();
        when(bookService.update(bookUpdateDto)).thenReturn(bookDto);

        when(bookService.findById(bookDto.getId())).thenReturn(bookDto);

        mockMvc.perform(patch("/api/book")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookDto)));

        verify(bookService).update(bookUpdateDto);
    }

    @DisplayName("Тест удаления книги")
    @Test
    void deleteBookByIdTest() throws Exception {
        mockMvc.perform(delete("/api/book/1")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookService).deleteById(1L);
    }
}