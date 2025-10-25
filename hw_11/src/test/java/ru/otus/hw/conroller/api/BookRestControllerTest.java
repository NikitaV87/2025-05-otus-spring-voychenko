package ru.otus.hw.conroller.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.conroller.rest.BookRestController;
import ru.otus.domain.Book;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.dto.ErrorResponse;
import ru.otus.dto.GenreDto;
import ru.otus.handler.ExceptionHandlerBeans;
import ru.otus.mapper.AuthorMapperImpl;
import ru.otus.mapper.BookMapper;
import ru.otus.mapper.BookMapperImpl;
import ru.otus.mapper.GenreMapperImpl;
import ru.otus.repositories.BookRepository;
import ru.otus.service.BookTemplateOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@DisplayName("Тест RestApi BookRestControllerTest")
@WebFluxTest(controllers = {BookRestController.class, BookMapperImpl.class, AuthorMapperImpl.class,
        ExceptionHandlerBeans.class, GenreMapperImpl.class})
@Import(value = ConfigurationTest.class)
public class BookRestControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    BookTemplateOperations bookTemplateOperations;

    @MockitoBean
    BookRepository bookRepository;

    @Autowired
    BookMapper bookMapper;

    public static final List<Book> BOOKS = List.of(
            Book.builder().id("1").title("Book_1").author("1").genres(List.of("1", "2")).build(),
            Book.builder().id("2").title("Book_2").author("2").genres(List.of("3", "4")).build());

    @DisplayName("Тест GET /api/book получить список книг")
    @Test
    void getAllBooksTest() {
        List<BookDto> booksDto = new ArrayList<>();
        for (Book book : BOOKS) {
            BookDto bookDto = bookMapper.toDto(book);
            bookDto.getAuthor().setFullName("Author_" + book.getAuthor());
            for (GenreDto genre : bookDto.getGenres()) {
                genre.setName("Genre_" + genre.getId());
            }
            booksDto.add(bookDto);
        }

        Flux<BookDto> bookFlux = Flux.fromIterable(booksDto);

        Mockito.when(bookTemplateOperations.findAll()).thenReturn(bookFlux);

        StepVerifier.create(
            webClient.get().uri("/api/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody())
                    .expectNextCount(2L)
                    .verifyComplete();

        Mockito.verify(bookTemplateOperations, times(1)).findAll();
    }

    @DisplayName("Тест GET /api/book/{id} получить книгу по id")
    @Test
    void getBookById() {
        Book book = BOOKS.get(0);

        Mockito.when(bookRepository.findById("1")).thenReturn(Mono.just(book));

        webClient.get().uri("/api/book/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(book));

        Mockito.verify(bookRepository, times(1)).findById("1");
    }

    @DisplayName("Тест DELETE /api/book/{id} удалить книгу по id")
    @Test
    void deleteBookByIdTest() {
        Mockito.when(bookRepository.deleteById("1")).thenReturn(Mono.empty());

        webClient.delete().uri("/api/book/{id}", "1")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    @DisplayName("Тест POST /api/book/{id} создать книгу")
    @Test
    void postBookTest() {
        Book book = BOOKS.get(0);
        BookCreateDto bookCreateDto = BookCreateDto.builder().title(book.getTitle())
                .authorId(book.getAuthor())
                .genreIds(book.getGenres()).build();

        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));

        webClient.post()
                .uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookCreateDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(BOOKS.get(0)));

        Mockito.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @DisplayName("Тест PUT /api/book обновить книгу")
    @Test
    void putBook() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor())
                .genreIds(book.getGenres())
                .build();

        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));

        webClient.put().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(book));

        Mockito.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @DisplayName("Тест валидации поле id")
    @Test
    void validateIdTest() {
        Book book = BOOKS.get(0);

        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .title(book.getTitle())
                .authorId(book.getAuthor())
                .genreIds(book.getGenres())
                .build();

        webClient.put().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("id", "Must fill Book id"))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест валидации поле title")
    @Test
    void validateTitleTest() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .authorId(book.getAuthor())
                .genreIds(book.getGenres())
                .build();

        webClient.put().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("title", "Must fill name of book"))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест валидации поле authorId")
    @Test
    void validateAuthorTest() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .genreIds(book.getGenres())
                .build();

        webClient.put().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("authorId", "Must select author of book"))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест валидации поле genreIds")
    @Test
    void validateFieldFillGenreTest() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor())
                .build();

        webClient.put().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("genreIds", "Must choice genre of book"))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }
}