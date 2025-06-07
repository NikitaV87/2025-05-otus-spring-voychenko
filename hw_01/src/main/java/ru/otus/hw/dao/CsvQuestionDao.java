package ru.otus.hw.dao;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.CSVQuestionSettings;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    private final CSVQuestionSettings csvQuestionSettings;

    @CsvBindByName(required = true)
    private String questionText;

    public CsvQuestionDao(AppProperties appProperties) {
        this.fileNameProvider = appProperties;
        this.csvQuestionSettings = appProperties;
    }

    @Override
    public List<Question> findAll() {
        try (Reader reader = new FileReader(fileNameProvider.getTestFileName())) {
            return new CsvToBeanBuilder<QuestionDto>(reader).withType(QuestionDto.class)
                    .withSeparator(csvQuestionSettings.getSeparatorCSVQuestion())
                    .withSkipLines(csvQuestionSettings.getSkipLines()).build().parse().stream()
                    .map(QuestionDto::toDomainObject).collect(Collectors.toList());
        } catch (IOException e) {
            throw new QuestionReadException("Error read file", e);
        }
    }
}
