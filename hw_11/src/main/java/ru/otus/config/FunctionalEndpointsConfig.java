package ru.otus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.dto.CommentDto;
import ru.otus.exception.NotFoundException;
import ru.otus.handler.CommentHandler;
import ru.otus.mapper.CommentMapper;
import ru.otus.repositories.CommentRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class FunctionalEndpointsConfig {

    private final CommentMapper mapper;

    private final CommentHandler handler;

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public RouterFunction<ServerResponse> composedRoutes(CommentRepository repository) {
        return route()
                .GET("/api/comment/{id}", accept(APPLICATION_JSON),
                        request -> repository.findById(request.pathVariable("id"))
                                .flatMap(comment -> ok().contentType(APPLICATION_JSON).body(fromValue(comment)))
                                .switchIfEmpty(Mono.error(new NotFoundException("Comment not have")))
                )
                .GET("/api/comment/book/{id}", accept(APPLICATION_JSON),
                        request -> ServerResponse.ok().contentType(APPLICATION_JSON).body(
                                repository.findByBookId(request.pathVariable("id")).map(mapper::toDto),
                                CommentDto.class)
                )
                .PATCH("/api/comment", handler::updateComment)
                .POST("/api/comment", handler::createComment)
                .DELETE("/api/comment/{id}", accept(APPLICATION_JSON), request ->
                        ServerResponse.noContent().build(repository.deleteById(request.pathVariable("id"))))
                .build();
    }
}
