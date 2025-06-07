package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final QuestionDao questionDao;

    private final IOService ioService;

    @Override
    public void executeTest() {
        List<Question> questions = questionDao.findAll();

        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        questions.forEach(question -> {
            System.out.println("Question " + question.text() + "\nAnswers: ");
            question.answers().forEach((t) -> ioService.printLine(t.text()));
            System.out.println();
        });
    }
}
