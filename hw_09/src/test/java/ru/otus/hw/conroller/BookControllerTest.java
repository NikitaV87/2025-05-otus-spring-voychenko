package ru.otus.hw.conroller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.BookController;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.mapper.response.ResponseCreateOrUpdateBookMapper;
import ru.otus.services.AuthorService;
import ru.otus.services.BookService;
import ru.otus.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = BookController.class)
class BookControllerTest {

    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private GenreService genreService;
    @MockitoBean
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;

    public static final List<AuthorDto> AUTHORS = List.of(new AuthorDto(1L, "Author_1"),
                                                          new AuthorDto(2L, "Author_2"));

    public static final List<GenreDto> GENRES = List.of(new GenreDto(1L, "Genre_1"),
                                                        new GenreDto(2L, "Genre_2"));

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

    public static final Long BOOK_ID = 1L;

    public static final Long BOOK_AUTHOR_ID = 2L;

    public static final Long BOOK_GENRE_ID = 2L;

    public static final String BOOK_TITLE = "Book title";

    @DisplayName("Тест выдачи книг")
    @Test
    void getAllBooksTest() throws Exception {
        when(bookService.findAll()).thenReturn(BOOKS);

        mockMvc.perform(get("/book" ))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book"))
                .andExpect(model().attribute("books", BOOKS));
    }

    @DisplayName("Тест показа формы для новой книги")
    @Test
    void getNewBookTest() throws Exception {
        when(authorService.findAll()).thenReturn(AUTHORS);
        when(genreService.findAll()).thenReturn(GENRES);


        mockMvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().attribute("authors", AUTHORS))
                .andExpect(model().attribute("genres",  GENRES));
    }

    @DisplayName("Тест создание новой книги")
    @Test
    void postNewBook() throws Exception {
        mockMvc
                .perform(post("/book/new")
                                .param("title", BOOK_TITLE)
                                .param("author", String.valueOf(BOOK_AUTHOR_ID))
                                .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book"))
                .andExpect(model().hasNoErrors());

        verify(bookService).insert(BOOK_TITLE, BOOK_AUTHOR_ID, List.of(BOOK_GENRE_ID));
    }

    @DisplayName("Тест валидации при создание новой книги")
    @Test
    void postNewBookValidationTest() throws Exception {
        mockMvc
                .perform(post("/book/new")
                        .param("title", BOOK_TITLE)
                        .param("author", "")
                        .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).insert(anyString(), anyLong(), ArgumentMatchers.anyList());

        mockMvc
                .perform(post("/book/new")
                        .param("title", "")
                        .param("author", String.valueOf(BOOK_AUTHOR_ID))
                        .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).insert(anyString(), anyLong(), ArgumentMatchers.anyList());

        mockMvc
                .perform(post("/book/new")
                        .param("title", BOOK_TITLE)
                        .param("author", String.valueOf(BOOK_AUTHOR_ID))
                        .param("genres", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).insert(anyString(), anyLong(), ArgumentMatchers.anyList());
    }

    @DisplayName("Тест показа формы для обновления книги")
    @Test
    void getUpdateBookTest() throws Exception {
        when(authorService.findAll()).thenReturn(AUTHORS);
        when(genreService.findAll()).thenReturn(GENRES);
        when(bookService.findById(1)).thenReturn(Optional.of(BOOKS.get(0)));


        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().attribute("authors", AUTHORS))
                .andExpect(model().attribute("genres",  GENRES))
                .andExpect(model().attribute("book", ResponseCreateOrUpdateBookMapper.toResponse(BOOKS.get(0))));
    }

    @DisplayName("Тест обновления книги")
    @Test
    void postUpdateNewBookTest() throws Exception {
        mockMvc.perform(post("/book/%d".formatted(BOOK_ID))
                        .param("title", BOOK_TITLE)
                        .param("author", String.valueOf(BOOK_AUTHOR_ID))
                        .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book"))
                .andExpect(model().hasNoErrors());

        verify(bookService).update(BOOK_ID, BOOK_TITLE, BOOK_AUTHOR_ID, List.of(BOOK_GENRE_ID));
    }

    @DisplayName("Тест валидации при обновления книги")
    @Test
    void postUpdateNewBookValidationTest() throws Exception {
        mockMvc.perform(post("/book/%d".formatted(BOOK_ID))
                        .param("title", "")
                        .param("author", String.valueOf(BOOK_AUTHOR_ID))
                        .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), ArgumentMatchers.anyList());

        mockMvc.perform(post("/book/%d".formatted(BOOK_ID))
                        .param("title", BOOK_TITLE)
                        .param("author", "")
                        .param("genres", String.valueOf(BOOK_GENRE_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), ArgumentMatchers.anyList());

        mockMvc.perform(post("/book/1")
                        .param("title", BOOK_TITLE)
                        .param("author", String.valueOf(BOOK_AUTHOR_ID))
                        .param("genres", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("book/form"))
                .andExpect(model().hasErrors());

        verify(bookService, never()).update(anyLong(), anyString(), anyLong(), ArgumentMatchers.anyList());
    }

    @DisplayName("Тест удаления книги")
    @Test
    void deleteByIdTest() throws Exception {
        mockMvc.perform(post("/book/%d/delete".formatted(BOOK_ID)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/book"))
                .andExpect(model().hasNoErrors());

        verify(bookService).deleteById(BOOK_ID);
    }
}