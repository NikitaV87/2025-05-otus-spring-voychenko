package ru.otus.conroller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import ru.otus.dto.ErrorResponse;
import ru.otus.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionAdviceRestController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleExceptionNotFound(NotFoundException exception) {
        log.info("handleExceptionNotFound: ", exception);

        return new ErrorResponse(Map.of("error", exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException.class)
    public ErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        log.info("handleWebExchangeBindException: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ErrorResponse(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("handleRuntimeException: {}", e.getMessage());
        e.printStackTrace();

        ErrorResponse errorResponse = new ErrorResponse(Map.of("Other", e.getMessage()));

        return new ResponseEntity<>(errorResponse , HttpStatus.BAD_REQUEST);
    }
}
