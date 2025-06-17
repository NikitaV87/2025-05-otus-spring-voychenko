package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties implements TestConfig, TestFileNameProvider {

    private int rightAnswersCountToPass;

    private String testFileName;

    private char separatorCSV;

    private int skipLinesCSV;

    public AppProperties(@Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                         @Value("${test.fileName}") String testFileName,
                         @Value("${test.separatorCSV}") char separatorCSV,
                         @Value("${test.skipLinesCSV}") int skipLinesCSV) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
        this.separatorCSV = separatorCSV;
        this.skipLinesCSV = skipLinesCSV;
    }
}
