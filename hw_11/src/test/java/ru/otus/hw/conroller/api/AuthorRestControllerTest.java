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
import reactor.core.publisher.Flux;
import ru.otus.conroller.rest.AuthorRestController;
import ru.otus.domain.Author;
import ru.otus.dto.AuthorDto;
import ru.otus.handler.ExceptionHandlerBeans;
import ru.otus.mapper.AuthorMapperImpl;
import ru.otus.repositories.AuthorRepository;

import java.util.List;

import static org.mockito.Mockito.times;

@DisplayName("Тест RestApi AuthorRestController")
@WebFluxTest(controllers = {AuthorRestController.class, AuthorMapperImpl.class, ExceptionHandlerBeans.class})
@Import(value = ConfigurationTest.class)
public class AuthorRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private AuthorRepository authorRepository;

    public static final  List<Author> AUTHORS = List.of(Author.builder().id("1").fullName("Author_1").build(),
            Author.builder().id("2").fullName("Author_2").build(),
            Author.builder().id("3").fullName("Author_3").build());

    @DisplayName("Тест GET /api/author - Получение списка авторов")
    @Test
    void getAuthorsTest() {
        Flux<Author> fluxAuthors = Flux.fromIterable(AUTHORS);

        Mockito.when(authorRepository.findAll()).thenReturn(fluxAuthors);

        webClient.get().uri("/api/author")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuthorDto.class);

        Mockito.verify(authorRepository, times(1)).findAll();
    }
}
