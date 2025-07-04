package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.*;

@DisplayName("Тест парсинга csv файлов")
public class CsvQuestionDaoTest {
    final public Map<String, List<Answer>> allQuestionAnswers;

    private final static Map<String, String> FILENAME_BY_LOCAL_TAG = Map.of(Locale.ENGLISH.toLanguageTag(), "questions.csv");
    private final static Map<String, String> NOT_HAVE_FILENAME_BY_LOCAL_TAG = Map.of(Locale.ENGLISH.toLanguageTag(), "NOT_HAVE_FILE.csv");

    {
        allQuestionAnswers = new LinkedHashMap<>() {{
            put("Is there life on Mars?", List.of(new Answer("Science doesn't know this yet", true),
                    new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                    new Answer("Absolutely not", false)));
            put("How should resources be loaded form jar in Java?",
                List.of(new Answer("ClassLoader#geResourceAsStream or ClassPathResource#getInputStream",
                                true),
                        new Answer("ClassLoader#geResource#getFile + FileReader", false),
                        new Answer("Wingardium Leviosa", false)));
            put("Which option is a good way to handle the exception?",
                    List.of(new Answer("@SneakyThrow", false), new Answer("e.printStackTrace()", false),
                            new Answer("Rethrow with wrapping in business exception (for example, QuestionRead" +
                                    "Exception)", true),
                            new Answer("Ignoring exception", false)));
        }};
    }

    @Test
    @DisplayName("Тест чтения файла csv")
    void testReadFileCSV() {
        AppProperties appProperties = new AppProperties(3, Locale.ENGLISH, FILENAME_BY_LOCAL_TAG, ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        Assertions.assertDoesNotThrow(() -> {
            сsvQuestionDao.findAll();
        }, "Ошибка чтения файла");
    }

    @Test
    @DisplayName("Проверка вопросов из файла")
    void testReadQuestionInFile() {
        AppProperties appProperties = new AppProperties(3, Locale.ENGLISH, FILENAME_BY_LOCAL_TAG, ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        List<Question> questions = сsvQuestionDao.findAll();

        Assertions.assertEquals(allQuestionAnswers.keySet().size(), questions.size(), "Неверное количество вопросов");

        Assertions.assertArrayEquals(allQuestionAnswers.keySet().toArray(String[]::new),
                questions.stream().map(Question::text).toArray(String[]::new),
                "Неожидаемый вопрос");
    }

    @Test
    @DisplayName("Проверка чтения ответов из файла")
    void testReadAnswersInFile() {
        AppProperties appProperties = new AppProperties(3, Locale.ENGLISH, FILENAME_BY_LOCAL_TAG, ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        List<Question> questions = сsvQuestionDao.findAll();

        for (Question question : questions) {
            String textQuestion = question.text();

            Assertions.assertTrue(allQuestionAnswers.containsKey(textQuestion),
                    "Некорректный вопрос: %s".formatted(textQuestion));

            Answer[] correctAnswers = allQuestionAnswers.get(textQuestion).toArray(Answer[]::new);
            Answer[] fileAnswers = question.answers().toArray(Answer[]::new);

            Assertions.assertEquals(correctAnswers.length, fileAnswers.length,
                    "Вопрос: %s\nНеверное количество ответов".formatted(textQuestion));

            Assertions.assertArrayEquals(correctAnswers, fileAnswers,
                    "вопросе: %s\nНекорректный ответ".formatted(textQuestion));
        }
    }

    @Test
    @DisplayName("Ошибка чтения отсутствующего файла")
    void testReadNotExistsFile() {
        AppProperties appProperties = new AppProperties(3, Locale.ENGLISH, NOT_HAVE_FILENAME_BY_LOCAL_TAG, ';', 1);
        CsvQuestionDao сsvQuestionDao = new CsvQuestionDao(appProperties);

        Assertions.assertThrows(QuestionReadException.class, сsvQuestionDao::findAll,
                "Нет ошибки при отсутствии файла");

    }
}
