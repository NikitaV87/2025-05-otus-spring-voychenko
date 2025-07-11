package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.service.LocalizedMessagesServiceImpl;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationEventsCommands {
    private final LocaleConfig localeConfig;

    private final LocalizedMessagesServiceImpl localizedMessagesService;

    private final TestRunnerService testRunnerService;

    /**
     * Сменить язык
     * @param locale язык
     * @return Сообщение о выполнении команды
     */
    @ShellMethod(value = "Change language", key = {"l", "language"})
    public String commandChangeLanguage(@ShellOption(defaultValue = "en-EN") String locale) {
        try {
            if (!localeConfig.getAvailableLanguageList().contains(locale)) {
                return localizedMessagesService.getMessage("ShellCommands.change.language.availability.options",
                        localeConfig.getAvailableLanguageList());
            }

            localeConfig.setLocale(locale);

            return localizedMessagesService.getMessage("ShellCommands.change.language.done", locale);
        } catch (IllegalArgumentException exception) {
            return exception.getMessage();
        }
    }

    /**
     * Запустить тест
     */
    @ShellMethod(value = "Test run", key = {"t", "test"})
    public void commandRunTest() {
            testRunnerService.run();
    }
}
