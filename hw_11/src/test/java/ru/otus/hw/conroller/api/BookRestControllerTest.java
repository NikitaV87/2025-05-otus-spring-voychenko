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
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.BookCreateDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.BookUpdateDto;
import ru.otus.dto.ErrorResponse;
import ru.otus.handler.ExceptionHandlerBeans;
import ru.otus.mapper.AuthorMapperImpl;
import ru.otus.mapper.BookMapper;
import ru.otus.mapper.BookMapperImpl;
import ru.otus.mapper.GenreMapperImpl;
import ru.otus.repositories.AuthorRepository;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.GenreRepository;

import static java.text.MessageFormat.format;

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
    BookRepository bookRepository;

    @MockitoBean
    GenreRepository genreRepository;

    @MockitoBean
    AuthorRepository authorRepository;

    @Autowired
    BookMapper bookMapper;

    public static final List<Book> BOOKS = List.of(
            Book.builder().id("1").title("Book_1")
                    .author(Author.builder().id("1").fullName("Author_1").build())
                     .genres(List.of(Genre.builder().id("1").name("Genre_1").build(),
                                     Genre.builder().id("2").name("Genre_2").build())).build(),
            Book.builder().id("2").title("Book_2")
                    .author(Author.builder().id("2").fullName("Author_2").build())
                    .genres(List.of(Genre.builder().id("3").name("Genre_3").build(),
                            Genre.builder().id("4").name("Genre_4").build())).build());

    @DisplayName("Тест GET /api/book получить список книг")
    @Test
    void getAllBooksTest() {
        Flux<Book> bookFlux = Flux.fromIterable(BOOKS);

        Mockito.when(bookRepository.findAll()).thenReturn(bookFlux);

        StepVerifier.create(
            webClient.get().uri("/api/book")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDto.class)
                .getResponseBody())
                    .expectNextCount(2L)
                    .verifyComplete();

        Mockito.verify(bookRepository, times(1)).findAll();
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
                .authorId(book.getAuthor().getId())
                .genreIds(book.getGenres().stream().map(Genre::getId).toList()).build();

        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(true));


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

    @DisplayName("Тест PATCH /api/book обновить книгу")
    @Test
    void patchBook() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreIds(book.getGenres().stream().map(Genre::getId).toList())
                .build();

        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(book));
        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(bookMapper.toDto(book));

        Mockito.verify(bookRepository, times(1)).save(any(Book.class));
    }

    @DisplayName("Тест проверки на существование книги при обновлении")
    @Test
    void checkBookExistsTest() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreIds(book.getGenres().stream().map(Genre::getId).toList())
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(false));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("error", format("Book {0} not found", book.getId())))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест проверки на существование жанра при обновлении")
    @Test
    void checkGenreExistsTest() {
        Book book = BOOKS.get(0);
        String genreId = book.getGenres().get(0).getId();
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreIds(List.of(genreId))
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        Mockito.when(genreRepository.existsById(genreId)).thenReturn(Mono.just(false));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("error", format("Genre not found: {0}", genreId)))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест проверки на существование автора при обновлении")
    @Test
    void checkAuthorExistsTest() {
        Book book = BOOKS.get(0);
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreIds(book.getGenres().stream().map(Genre::getId).toList())
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/book")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(bookUpdateDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("error", format("Author not found: {0}", book.getAuthor().getId())))
                        .build());

        Mockito.verify(bookRepository, times(0)).save(any(Book.class));
    }

    @DisplayName("Тест валидации поле id")
    @Test
    void validateIdTest() {
        Book book = BOOKS.get(0);
        String genreId = book.getGenres().get(0).getId();
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .title(book.getTitle())
                .authorId(book.getAuthor().getId())
                .genreIds(List.of(genreId))
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/book")
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
        String genreId = book.getGenres().get(0).getId();
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .authorId(book.getAuthor().getId())
                .genreIds(List.of(genreId))
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/book")
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
        String genreId = book.getGenres().get(0).getId();
        BookUpdateDto bookUpdateDto = BookUpdateDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .genreIds(List.of(genreId))
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/book")
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
                .authorId(book.getAuthor().getId())
                .build();

        Mockito.when(bookRepository.existsById(book.getId())).thenReturn(Mono.just(true));
        book.getGenres().forEach(genre -> Mockito.when(genreRepository.existsById(genre.getId()))
                .thenReturn(Mono.just(true)));
        Mockito.when(authorRepository.existsById(book.getAuthor().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/book")
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