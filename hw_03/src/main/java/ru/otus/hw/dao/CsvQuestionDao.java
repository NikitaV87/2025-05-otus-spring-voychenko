package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        ClassPathResource resource = new ClassPathResource(fileNameProvider.getTestFileName());

        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            return new CsvToBeanBuilder<QuestionDto>(reader).withType(QuestionDto.class)
                    .withSeparator(fileNameProvider.getSeparatorCSV())
                    .withSkipLines(fileNameProvider.getSkipLinesCSV()).build().parse().stream()
                    .map(QuestionDto::toDomainObject).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            throw new QuestionReadException("File %s not found".formatted(resource.getFilename()));
        } catch (IOException e) {
            throw new QuestionReadException("Error read file", e);
        }
    }
}
