package ru.otus.hw.conroller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.controller.rest.GenreRestController;
import ru.otus.dto.GenreDto;
import ru.otus.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GenreRestController.class)
public class GenreRestControllerTest {

    @MockitoBean
    GenreService genreService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    public static final List<GenreDto> GENRES = List.of(new GenreDto(1L, "Genre_1"),
            new GenreDto(2L, "Genre_2"));

    @DisplayName("Тест показа всех аторов")
    @Test
    void getAllGenresTest() throws Exception {
        when(genreService.findAll()).thenReturn(GENRES);

        mockMvc.perform(get("/api/genre"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(GENRES)));
    }
}
