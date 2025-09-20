package ru.otus.hw.conroller.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.view.GenreController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = GenreController.class)
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Тест показа всех аторов")
    @Test
    void getAllGenresTest() throws Exception {
        mockMvc.perform(get("/genre"))
                .andExpect(status().isOk())
                .andExpect(view().name("genre/genre"));
    }
}
