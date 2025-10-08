package ru.otus.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentCreateDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.CommentUpdateDto;
import ru.otus.exception.NotFoundException;
import ru.otus.mapper.CommentMapper;
import ru.otus.repositories.BookRepository;
import ru.otus.repositories.CommentRepository;

import java.beans.Introspector;

import static java.text.MessageFormat.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@AllArgsConstructor
@Slf4j
public class CommentHandler {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper mapper;

    private final SpringValidatorAdapter validator;


    public Mono<ServerResponse> createComment(ServerRequest request) {
        Mono<CommentDto> commentDto = request.bodyToMono(CommentCreateDto.class)
                .doOnNext(this::validate)
                .map(mapper::fromCreateDto)
                .flatMap(comment -> checkBookExists(Mono.just(comment)))
                .flatMap(commentRepository::save)
                .map(mapper::toDto);

        return ServerResponse.status(HttpStatus.CREATED).contentType(APPLICATION_JSON)
                .body(commentDto, CommentDto.class);
    }

    public Mono<ServerResponse> updateComment(ServerRequest request) {
        Mono<CommentDto> commentDto = request.bodyToMono(CommentUpdateDto.class)
                .doOnNext(this::validate)
                .map(mapper::fromUpdDto)
                .flatMap(comment -> checkCommentExists(Mono.just(comment)))
                .flatMap(comment -> checkBookExists(Mono.just(comment)))
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
                log.error("no validate");
                throw new WebExchangeBindException(
                        new MethodParameter(this.getClass().getDeclaredMethod("validate", Object.class), 0),
                        errors);
           } catch (NoSuchMethodException e) {
               throw new RuntimeException(e);
           }
        }
    }

    private Mono<Comment> checkCommentExists(Mono<Comment> commentMono) {
        return commentMono.flatMap(comment ->
                commentRepository.existsById(comment.getId()).flatMap(isExists -> {
                    if (!isExists) {
                        return Mono.error(new NotFoundException("Comment not have"));
                    }
                    return Mono.just(true);
                }).map(isExists -> comment));
    }

    private Mono<Comment> checkBookExists(Mono<Comment> commentMono) {
        return commentMono.flatMap(comment ->
                bookRepository.existsById(comment.getBook().getId()).flatMap(isExists -> {
                    if (!isExists) {
                        return Mono.error(new NotFoundException(
                                format("Book {0} not found", comment.getBook().getId())));
                    }

                    return Mono.just(true);
                }).map(isExists -> comment));
    }
}
