package ru.otus.hw.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AppProperties implements TestFileNameProvider, CSVQuestionSettings {
    private String testFileName;

    private char separatorCSVQuestion;

    private int skipLines;
}
