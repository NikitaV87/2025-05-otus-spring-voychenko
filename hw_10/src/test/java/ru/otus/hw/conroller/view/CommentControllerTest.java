package ru.otus.hw.conroller.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.view.CommentController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("Тест выдачи view /comment/book")
    @Test
    void getAllBooksTest() throws Exception {
        mockMvc.perform(get("/comment/book"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/book"));
    }

    @DisplayName("Тест выдачи view /comment/book/{id}")
    @Test
    void getCommentsByBookTest() throws Exception {
        mockMvc.perform(get("/comment/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/comments"));
    }

    @DisplayName("Тест выдачи view /comment/{id}")
    @Test
    void getUpdateCommentTest() throws Exception {
        mockMvc.perform(get("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/formUpd"));
    }

    @DisplayName("Тест выдачи view /comment/new/book/{id}")
    @Test
    void getNewCommentTest() throws Exception {
        mockMvc.perform(get("/comment/new/book/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("comment/formCreate"));
    }
}
