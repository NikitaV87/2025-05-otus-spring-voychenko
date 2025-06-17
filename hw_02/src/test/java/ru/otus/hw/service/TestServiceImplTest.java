package ru.otus.hw.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("Тест работы с пользователем")
public class TestServiceImplTest {

    private final static int COUNT_CORRECT_ANSWERS = 1;

    @Spy
    private IOService ioService = Mockito.mock(IOService.class);

    @Mock
    private QuestionDao questionDao = Mockito.mock(QuestionDao.class);

    private final TestService testServiceImpl = new TestServiceImpl(ioService, questionDao);


    @DisplayName("Тест ввода ответа на вопросы")
    @Test
    void executeTestFor() {
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(1, 1);

        when(questionDao.findAll()).thenReturn(
                List.of(new Question("question1",
                                Arrays.asList(new Answer("one", false),
                                        new Answer("two", true),
                                        new Answer("three", false))),
                        new Question("question2",
                                Arrays.asList(new Answer("one", false),
                                        new Answer("two", false),
                                        new Answer("three", true)))
                )
        );

        Student student = new Student("Ivan", "Ivanov");

        TestResult TestResult = testServiceImpl.executeTestFor(student);

        Assertions.assertEquals(questionDao.findAll().size(), TestResult.getAnsweredQuestions().size(),
                "Количество вопросов не совпадает");

        Assertions.assertEquals(COUNT_CORRECT_ANSWERS, TestResult.getRightAnswersCount(),
                "Количество правильных ответов не совпадает");
    }
}
