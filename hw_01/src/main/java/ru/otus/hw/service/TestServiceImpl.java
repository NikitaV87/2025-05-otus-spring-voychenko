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

        questions.parallelStream().forEachOrdered(question -> {
            ioService.printLine("Question " + question.text() + "\nAnswers: ");

            final int[] index = new int[]{0};

            question.answers().forEach((t) -> ioService.printLine((++index[0]) + ": " + t.text()));

            ioService.printLine("");
        });
    }
}
