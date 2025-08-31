package ru.otus.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ExceptionAdviceController {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception) {
        log.error("INTERNAL_SERVER_ERROR: ", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/message");
        modelAndView.addObject("message",
                "There is an error on the server, please try again later");

        return modelAndView;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ModelAndView handleNotFoundException(Exception exception) {
        log.error("NOT_FOUND: ", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/message");
        modelAndView.addObject("message", "Page not found");

        return modelAndView;
    }
}
