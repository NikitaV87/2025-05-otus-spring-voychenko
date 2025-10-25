package ru.otus.handler;

import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.mapper.CommentMapper;
import ru.otus.repositories.CommentRepository;

import java.beans.Introspector;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@AllArgsConstructor
public class CommentHandler {

    private final CommentRepository commentRepository;

    private final CommentMapper mapper;

    private final SpringValidatorAdapter validator;


    public Mono<ServerResponse> createComment(ServerRequest request) {
        Mono<CommentDto> commentDto = request.bodyToMono(CommentCreateDto.class)
                .doOnNext(this::validate)
                .map(mapper::fromCreateDto)
                .flatMap(commentRepository::save)
                .map(mapper::toDto);

        return ServerResponse.status(HttpStatus.CREATED).contentType(APPLICATION_JSON)
                .body(commentDto, CommentDto.class);
    }

    public Mono<ServerResponse> updateComment(ServerRequest request) {
        Mono<CommentDto> commentDto = request.bodyToMono(CommentUpdateDto.class)
                .doOnNext(this::validate)
                .map(mapper::fromUpdDto)
                .flatMap(commentRepository::save)
                .map(mapper::toDto);

        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(commentDto, CommentDto.class);
    }

    private void validate(Object obj) throws WebExchangeBindException {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(obj,
                Introspector.decapitalize(obj.getClass().getSimpleName()));
        validator.validate(obj, errors);

        if (errors.hasErrors()) {
            try {
                throw new WebExchangeBindException(
                        new MethodParameter(this.getClass().getDeclaredMethod("validate", Object.class), 0),
                        errors);
           } catch (NoSuchMethodException e) {
               throw new RuntimeException(e);
           }
        }
    }
}
