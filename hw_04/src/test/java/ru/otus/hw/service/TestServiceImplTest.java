package ru.otus.hw.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestServiceImpl.class)
@DisplayName("Тест работы с пользователем")
public class TestServiceImplTest {

    private final static int COUNT_CORRECT_ANSWERS = 1;

    @MockitoBean
    private LocalizedIOService ioService = Mockito.mock(LocalizedIOService.class);

    @MockitoBean
    private QuestionDao questionDao = Mockito.mock(QuestionDao.class);

    @Autowired
    private TestService testServiceImpl;


    @DisplayName("Тест ввода ответа на вопросы")
    @Test
    void executeTestFor() {
        when(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString())).thenReturn(2, 2);

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
