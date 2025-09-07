package ru.otus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.exceptions.EntityNotFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionAdviceController {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleExceptionNotFound(EntityNotFoundException exception) {
        log.error("NOT_FOUND: ", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/message");
        modelAndView.addObject("message", exception.getReason());
        modelAndView.setStatus(HttpStatus.NOT_FOUND);

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleExceptionOther(Exception exception) {
        log.error("INTERNAL_SERVER_ERROR: ", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/message");
        modelAndView.addObject("message", "Sorry we have some problems, please try again a later");

        return modelAndView;
    }
}
