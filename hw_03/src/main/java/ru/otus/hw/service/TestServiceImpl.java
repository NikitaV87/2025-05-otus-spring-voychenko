package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            outQuestionAndAnswers(question);

            var isAnswerValid = getSelectedAnswer(question).isCorrect();

            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private Answer getSelectedAnswer(Question question) {
        int countAnswer = question.answers().size() - 1;
        int indexAnswer = ioService.readIntForRangeLocalized(0, countAnswer, "TestService.question.repeat");

        return question.answers().get(indexAnswer);
    }

    private void outQuestionAndAnswers(Question question) {
        ioService.printFormattedLine(question.text());
        ioService.printFormattedLine(getNumberedStringAnswers(question));
    }

    private String getNumberedStringAnswers(Question question) {
        StringBuilder resultText = new StringBuilder();

        for (int answerIndex = 0; answerIndex < question.answers().size(); answerIndex++) {
            resultText.append(String.format("%d: %s\n", answerIndex, question.answers().get(answerIndex).text()));
        }

        return resultText.toString();
    }
}
