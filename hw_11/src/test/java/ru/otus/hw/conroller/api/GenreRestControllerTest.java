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
import ru.otus.conroller.rest.GenreRestController;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;
import ru.otus.handler.ExceptionHandlerBeans;
import ru.otus.mapper.GenreMapperImpl;
import ru.otus.repositories.GenreRepository;

import java.util.List;

import static org.mockito.Mockito.times;

@DisplayName("Тест RestApi GenreRestController")
@WebFluxTest(controllers = {GenreRestController.class, GenreMapperImpl.class, ExceptionHandlerBeans.class})
@Import(value = ConfigurationTest.class)
public class GenreRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    GenreRepository genreRepository;

    public static final List<Genre> GENRES = List.of(Genre.builder().id("1").name("Genre_1").build(),
            Genre.builder().id("2").name("Genre_2").build(),
            Genre.builder().id("3").name("Genre_3").build(),
            Genre.builder().id("4").name("Genre_4").build(),
            Genre.builder().id("5").name("Genre_5").build(),
            Genre.builder().id("6").name("Genre_6").build());

    @DisplayName("Тест GET /api/genre получить список жанров")
    @Test
    void getGenreList() {
        Flux<Genre> fluxGenres = Flux.fromIterable(GENRES);

        Mockito.when(genreRepository.findAll()).thenReturn(fluxGenres);

        webClient.get().uri("/api/genre")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GenreDto.class);

        Mockito.verify(genreRepository, times(1)).findAll();
    }
}
