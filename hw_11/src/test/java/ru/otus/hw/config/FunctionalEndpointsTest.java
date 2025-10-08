package ru.otus.hw.config;

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
import ru.otus.config.FunctionalEndpointsConfig;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.dto.ErrorResponse;
import ru.otus.handler.CommentHandler;
import ru.otus.handler.ExceptionHandlerBeans;
import ru.otus.mapper.AuthorMapperImpl;
import ru.otus.mapper.BookMapperImpl;
import ru.otus.mapper.CommentMapper;
import ru.otus.mapper.CommentMapperImpl;
import ru.otus.mapper.GenreMapperImpl;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.CommentRepository;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@DisplayName("Тест RestApi end points FunctionalEndpointsConfig")
@WebFluxTest(controllers = {CommentHandler.class, ExceptionHandlerBeans.class, CommentMapperImpl.class,
        BookMapperImpl.class, AuthorMapperImpl.class, GenreMapperImpl.class})
@Import(value = {FunctionalEndpointsConfig.class})
public class FunctionalEndpointsTest {
    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    CommentRepository commentRepository;

    @MockitoBean
    BookRepository bookRepository;

    @Autowired
    CommentMapper commentMapper;

    private final List<Comment> COMMENTS = List.of(Comment.builder().id("1").text("Comment_1").book(
            Book.builder().id("1").title("Book_1").author(Author.builder().id("1").fullName("Author_1").build())
                .genres(List.of(Genre.builder().id("1").name("Genre_1").build())).build()).build(),
        Comment.builder().id("2").text("Comment_2").book(
             Book.builder().id("1").title("Book_1").author(Author.builder().id("1").fullName("Author_1").build())
                .genres(List.of(Genre.builder().id("1").name("Genre_1").build())).build()).build());

    @DisplayName("Тест GET /api/comment/{id} получить комментарий по id")
    @Test
    void getCommentByIdTest() {
        Comment comment = COMMENTS.get(0);
        Mono<Comment> commentMono = Mono.just(comment);

        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(commentMono);

        webClient.get().uri("/api/comment/{id}", comment.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(commentMapper.toDto(comment));

        Mockito.verify(commentRepository, times(1)).findById(comment.getId());
    }

    @DisplayName("Тест GET /api/comment/book/{id} получить комментарии книги по id книги")
    @Test
    void getCommentByBookIdTest() {
        Flux<Comment> bookFlux = Flux.fromIterable(COMMENTS);

        Mockito.when(commentRepository.findByBookId("1")).thenReturn(bookFlux);

        StepVerifier.create(webClient.get().uri("/api/comment/book/{id}", "1")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(CommentDto.class)
                    .getResponseBody())
                .expectNextCount(2L)
                .verifyComplete();

        Mockito.verify(commentRepository, times(1)).findByBookId("1");
    }
    @DisplayName("Тест PATCH /api/comment обновить комментарий")
    @Test
    void patchCommentTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.existsById("1")).thenReturn(Mono.just(true));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(commentMapper.toDto(comment));

        Mockito.verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("Тест POST /api/comment создание комментария")
    @Test
    void postCommentTest() {
        Comment comment = COMMENTS.get(0);
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text(comment.getText())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.post().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentCreateDto))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CommentDto.class)
                .isEqualTo(commentMapper.toDto(comment));

        Mockito.verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("Тест DELETE /api/comment/{id} удалить комментарий по id")
    @Test
    void deleteBookByIdTest() {
        Mockito.when(commentRepository.deleteById("1")).thenReturn(Mono.empty());

        webClient.delete().uri("/api/comment/{id}", "1")
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    @DisplayName("Тест проверки PATCH /api/comment на существование комментария")
    @Test
    void checkExistsCommentTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.existsById(comment.getId())).thenReturn(Mono.just(false));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("error", "Comment not have"))
                        .build());

        Mockito.verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @DisplayName("Тест проверки PATCH /api/comment на существование книги")
    @Test
    void checkExistsBookTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.existsById(comment.getId())).thenReturn(Mono.just(true));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(false));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("error", format("Book {0} not found", comment.getBook().getId())))
                        .build());

        Mockito.verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @DisplayName("Тест валидации поля text PATCH /api/comment на заполнение текста комментария")
    @Test
    void validateTextTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(comment.getId())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.existsById(comment.getId())).thenReturn(Mono.just(true));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("text", "It is necessary to fill in the text of the comment"))
                        .build());

        Mockito.verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @DisplayName("Тест валидации поля id PATCH /api/comment на заполнение id комментария")
    @Test
    void validateIdTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .text(comment.getText())
                .bookId(comment.getBook().getId())
                .build();

        Mockito.when(commentRepository.existsById(comment.getId())).thenReturn(Mono.just(true));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("id", "Comment is not exists"))
                        .build());

        Mockito.verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @DisplayName("Тест валидации поля bookId PATCH /api/comment на заполнение id книги")
    @Test
    void validateBookIdTest() {
        Comment comment = COMMENTS.get(0);
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .build();

        Mockito.when(commentRepository.existsById(comment.getId())).thenReturn(Mono.just(true));
        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(Mono.just(comment));
        Mockito.when(bookRepository.existsById(comment.getBook().getId())).thenReturn(Mono.just(true));

        webClient.patch().uri("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(commentUpdateDto))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .isEqualTo(ErrorResponse.builder()
                        .errors(Map.of("bookId", "You need to choose a book"))
                        .build());

        Mockito.verify(commentRepository, times(0)).save(any(Comment.class));
    }
}
