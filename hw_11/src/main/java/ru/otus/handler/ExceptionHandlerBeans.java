package ru.otus.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.dto.ErrorResponse;
import ru.otus.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@Component
@Order(-2)
public class ExceptionHandlerBeans extends AbstractErrorWebExceptionHandler {
    public ExceptionHandlerBeans(ErrorAttributes errorAttributes,
                                 Resources resources,
                                 ApplicationContext applicationContext,
                                 ServerCodecConfigurer configure) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configure.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), (serverRequest -> {
            var throwable = errorAttributes.getError(serverRequest);
            if (throwable instanceof WebExchangeBindException) {
                return handleValidationException((WebExchangeBindException) throwable);
            }
            if (throwable instanceof NotFoundException) {
                return ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(
                        new ErrorResponse(Map.of("error", throwable.getMessage())));
            }

            log.error("Ops, just caught an unknown exception, " +
                    "please have a look at the stack trace of more details", throwable);

            return ServerResponse.status(INTERNAL_SERVER_ERROR).build();
        }));
    }

    private Mono<ServerResponse> handleValidationException(WebExchangeBindException exception) {
        log.error("handleValidationException: {}", exception.getMessage());
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ServerResponse.badRequest().bodyValue(new ErrorResponse(errors));
    }
}