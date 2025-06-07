package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@DisplayName("Тест парсинга csv файлов")
public class CsvQuestionDaoTest {
    final public HashMap<String, List<Answer>> allQuestionAnswers;

    {
        allQuestionAnswers = new HashMap<>() {{
            put("Is there life on Mars?", List.of(new Answer("Science doesn't know this yet", true),
                    new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                    new Answer("Absolutely not", false)));
            put("How should resources be loaded form jar in Java?",
                    List.of(new Answer("ClassLoader#geResourceAsStream or ClassPathResource#getInputStream", true),
                            new Answer("ClassLoader#geResource#getFile + FileReader", false),
                            new Answer("Wingardium Leviosa", false)));
            put("Which option is a good way to handle the exception?",
                    List.of(new Answer("@SneakyThrow", false), new Answer("e.printStackTrace()", false), new Answer(
                                    "Rethrow with wrapping in business exception (for example, QuestionReadException)", true),
                            new Answer("Ignoring exception", false)));
        }};
    }

    @Test
    @DisplayName("Тест чтения файла csv")
    void testReadFileCSV() {
        AppProperties appProperties = new AppProperties("src/test/java/resources/questions.csv", ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        Assertions.assertDoesNotThrow(() -> {
            сsvQuestionDao.findAll();
        }, "Ошибка чтения файла");
    }

    @Test
    @DisplayName("Проверка вопросов из файла")
    void testReadQuestionInFile() {
        AppProperties appProperties = new AppProperties("src/test/java/resources/questions.csv", ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        List<Question> questions = сsvQuestionDao.findAll();

        Assertions.assertEquals(allQuestionAnswers.keySet().size(), questions.size(), "Неверное количество вопросов");

        Assertions.assertArrayEquals(questions.stream().map(Question::text).sorted().toArray(String[]::new),
                allQuestionAnswers.keySet().stream().sorted().toArray(String[]::new), "Неожидаемый вопрос");
    }

    @Test
    @DisplayName("Проверка чтения ответов из файла")
    void testReadAnswersInFile() {
        AppProperties appProperties = new AppProperties("src/test/java/resources/questions.csv", ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        List<Question> questions = сsvQuestionDao.findAll();

        for (Question question : questions) {
            String textQuestion = question.text();

            Assertions.assertTrue(allQuestionAnswers.containsKey(textQuestion),
                    "Некорректный вопрос: %s".formatted(textQuestion));

            Answer[] correctAnswers = allQuestionAnswers.get(textQuestion).stream()
                    .sorted(Comparator.comparing(Answer::text)).toArray(Answer[]::new);
            Answer[] fileAnswers = question.answers().stream().sorted(Comparator.comparing(Answer::text))
                    .toArray(Answer[]::new);

            Assertions.assertEquals(correctAnswers.length, fileAnswers.length,
                    "Вопрос: %s\nНеверное количество ответов".formatted(textQuestion));

            Assertions.assertArrayEquals(correctAnswers, fileAnswers,
                    "вопросе: %s\nНекорректный ответ".formatted(textQuestion));
        }
    }

    @Test
    @DisplayName("Ошибка чтения отсутствующего файла")
    void testReadNotExistsFile() {
        AppProperties appProperties = new AppProperties("src/test/java/resources/NO_HAVE.csv", ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        Assertions.assertThrows(QuestionReadException.class, () -> {
            сsvQuestionDao.findAll();
        }, "Нет ошибки при отсутствии файла");

    }
}
