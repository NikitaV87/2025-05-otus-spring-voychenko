package ru.otus.hw.config;

public interface TestFileNameProvider {
    String getTestFileName();

    char getSeparatorCSV();

    int getSkipLinesCSV();
}
