package edu.marcos.saxtracker.controller;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExerciseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddTheExercise() throws Exception {
        mockMvc.perform(
                post("/exercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Escala de Do\",\"type\":\"SCALE\"}")
        ).andExpect(status().)
                .andExpect(jsonPath("$.name").value("Escala de Do"));


    }

}
