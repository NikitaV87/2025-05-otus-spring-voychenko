package ru.otus.hw.conroller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.rest.AuthorRestController;
import ru.otus.dto.AuthorDto;
import ru.otus.services.AuthorService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthorRestController.class)
public class AuthorRestControllerTest {

    @MockitoBean
    private AuthorService authorService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public static final List<AuthorDto> AUTHORS = List.of(new AuthorDto(1L, "Author_1"),
            new AuthorDto(2L, "Author_2"));

    @DisplayName("Тест показа всех авторов")
    @Test
    void getAllAuthorsTest() throws Exception {
        when(authorService.findAll()).thenReturn(AUTHORS);


        mockMvc.perform(get("/api/author"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(AUTHORS)));
    }
}
