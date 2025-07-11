package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CsvQuestionDao.class)
@DisplayName("Тест парсинга csv файлов")
public class CsvQuestionDaoTest {
    @Autowired
    private QuestionDao questionDao;

    @MockitoBean
    private TestFileNameProvider appProperties;

    final public Map<String, List<Answer>> allQuestionAnswers;

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

    @BeforeEach
    public void setUp() {
        Mockito.when(appProperties.getTestFileName()).thenReturn("questions.csv");
        Mockito.when(appProperties.getSeparatorCSV()).thenReturn(';');
        Mockito.when(appProperties.getSkipLinesCSV()).thenReturn(1);
    }

    @Test
    @DisplayName("Тест чтения файла csv")
    void testReadFileCSV() {
        Assertions.assertDoesNotThrow(() -> {
            questionDao.findAll();
        }, "Ошибка чтения файла");
    }

    @Test
    @DisplayName("Проверка вопросов из файла")
    void testReadQuestionInFile() {
        List<Question> questions = questionDao.findAll();

        Assertions.assertEquals(allQuestionAnswers.keySet().size(), questions.size(), "Неверное количество вопросов");

        Assertions.assertArrayEquals(allQuestionAnswers.keySet().toArray(String[]::new),
                questions.stream().map(Question::text).toArray(String[]::new),
                "Неожидаемый вопрос");
    }

    @Test
    @DisplayName("Проверка чтения ответов из файла")
    void testReadAnswersInFile() {
        List<Question> questions = questionDao.findAll();

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
        Mockito.when(appProperties.getTestFileName()).thenReturn("file_not_have.csv");

        Assertions.assertThrows(QuestionReadException.class, questionDao::findAll,
                "Нет ошибки при отсутствии файла");
    }
}
