package edu.marcos.saxtracker.controller;


import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExerciseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private Long createExercise(String name, String type) throws Exception {
        String response = mockMvc.perform(
                        post("/exercicios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"" + name + "\",\"type\":\"" + type + "\"}")
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    @Test
    void shouldAddTheExercise() throws Exception {
        mockMvc.perform(
                post("/exercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Escala de Do\",\"type\":\"SCALE\"}")
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Escala de Do"));
    }
    @Test
    void shouldGetAllExercises() throws Exception {
        this.createExercise("NameTest", "SCALE");
        mockMvc.perform(
                get("/exercicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("NameTest"));
    }

    @Test
    void shouldGetExerciseById() throws Exception {
        Long id = this.createExercise("NameTest", "SCALE");
        mockMvc.perform(
                get("/exercicios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NameTest"));

    }
    @Test
    void shouldGetAllExercisesFilteredByType() throws Exception {
        this.createExercise("NameTest1", "SCALE");
        this.createExercise("NameTest2", "BREATH_SPEED");
        this.createExercise("NameTest3", "SCALE");
        mockMvc.perform(
                        get("/exercicios?type=SCALE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("NameTest1"))
                .andExpect(jsonPath("$[1].name").value("NameTest3"));
    }


}
