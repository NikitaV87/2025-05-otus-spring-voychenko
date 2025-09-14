package ru.otus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.repositories.CommentRepository;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FunctionalEndpointsConfig {
    @Bean
    public RouterFunction<ServerResponse> composedRoutes(CommentRepository repository) {
        return route()
                .GET("/api/comment/{id}")
                .GET("/api/comment/book/{id}")
                .PATCH("/api/comment")
                .POST("/api/comment")
                .DELETE("/api/comment/{id}")
    }
}
