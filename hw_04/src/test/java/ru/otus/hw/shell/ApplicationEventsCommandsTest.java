package ru.otus.hw.shell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.LocaleConfig;
import ru.otus.hw.service.LocalizedMessagesServiceImpl;
import ru.otus.hw.service.TestRunnerService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@DisplayName("Тест shell")
@ShellTest
public class ApplicationEventsCommandsTest {
    @Autowired
    ShellTestClient client;

    @MockitoBean
    private LocaleConfig localeConfig;

    @MockitoBean
    private LocalizedMessagesServiceImpl localizedMessagesService;

    @MockitoBean
    private TestRunnerService testRunnerService;

    @DisplayName("Тест команды help")
    @Test
    void testHelpCommand() {
        ShellTestClient.InteractiveShellSession session = client.interactive().run();

        session.write(session.writeSequence().text("help").carriageReturn().build());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen()).containsText("AVAILABLE COMMANDS");
            ShellAssertions.assertThat(session.screen()).containsText("t, test: Test run");
            ShellAssertions.assertThat(session.screen()).containsText("l, language: Change language");
        });
    }

    @DisplayName("Тест смены языка")
    @Test
    void testCommandChangeLanguage() {
        Set<String> availabilityLanguage = new HashSet<>(Arrays.asList("ru-RU", "en-US"));

        Mockito.when(localeConfig.getAvailableLanguageList()).thenReturn(availabilityLanguage);

        ShellTestClient.InteractiveShellSession session = client.interactive().run();

        session.write(session.writeSequence().text("l ru-RU").carriageReturn().build());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Mockito.verify(localeConfig).setLocale("ru-RU");
        });
    }

    @DisplayName("Тест смены языка на несуществующий")
    @Test
    void testCommandChangeUnavailableLanguage() {
        Set<String> availabilityLanguage = new HashSet<>(Arrays.asList("ru-RU", "en-US"));
        String unavailableLanguage = "fr-FR";

        Mockito.when(localeConfig.getAvailableLanguageList()).thenReturn(availabilityLanguage);

        ShellTestClient.InteractiveShellSession session = client.interactive().run();

        session.write(session.writeSequence().text("l " + unavailableLanguage).carriageReturn().build());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Mockito.verify(localizedMessagesService).getMessage("ShellCommands.change.language.availability.options", availabilityLanguage);
        });
    }

    @DisplayName("Тест запуска теста")
    @Test
    void testCommandRunTest() {
        ShellTestClient.InteractiveShellSession session = client.interactive().run();

        session.write(session.writeSequence().text("test").carriageReturn().build());

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            Mockito.verify(testRunnerService).run();
        });
    }
}
