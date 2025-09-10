package ru.otus.hw.conroller.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.view.BookController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Тест выдачи view для /book")
    @Test
    void getAllBooksTest() throws Exception {

        mockMvc.perform(get("/book" ))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book"));
    }

    @DisplayName("Тест выдачи view для /book/new")
    @Test
    void getNewBookTest() throws Exception {

        mockMvc.perform(get("/book/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/formCreate"));
    }

    @DisplayName("Тест выдачи view для /book/{%id}")
    @Test
    void getUpdateBookTest() throws Exception {
        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/formUpd"))
                .andExpect(model().attribute("bookId", 1L));
    }
}
