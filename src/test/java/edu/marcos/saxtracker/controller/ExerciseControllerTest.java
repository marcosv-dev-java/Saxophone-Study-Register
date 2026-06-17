package edu.marcos.saxtracker.controller;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private Boolean deactivateExercise(Long id)throws Exception{
        String response = mockMvc.perform(
                delete("/exercicios/"+id+"/desativar")
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.active");
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
    @Test
    void shouldUpdateExerciseById() throws Exception {
        Long id = this.createExercise("exercise name","SCALE");
        mockMvc.perform(
                patch("/exercicios/"+ id)
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Escala de Do\"}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Escala de Do"))
                .andExpect(jsonPath("$.type").value("SCALE"));
    }
    @Test
    void shouldReactivateExerciseById() throws Exception{
        Long id = this.createExercise("exercise name","SCALE");
        Boolean exerciseActivate = deactivateExercise(id);
        Assertions.assertFalse(exerciseActivate);

        mockMvc.perform(
                patch("/exercicios/"+ id+"/reativar")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
    @Test
    void shouldHardDeleteExerciseById() throws Exception {
        Long id = this.createExercise("exercise name","SCALE");
        mockMvc.perform(
                delete("/exercicios/"+ id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/exercicios/" + id))
                .andExpect(status().isNotFound());

    }
    @Test
    void shouldDeactivateExerciseById() throws Exception {
        Long id = this.createExercise("name","SCALE");
        Boolean exerciseDeactivate = deactivateExercise(id);
        Assertions.assertFalse(exerciseDeactivate);
    }
    @Test
    void shouldNotAddExerciseWithBlankName() throws Exception {
        mockMvc.perform(
                post("/exercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"type\": \"SCALE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("name: must not be blank")));

    }
    @Test
    void shouldNotAddExerciseWithNullType() throws Exception {
        mockMvc.perform(
                post("/exercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"a nice name\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("type: must not be null")));

    }
    @Test
    void shouldNotAddExerciseWithAlreadyExists() throws Exception {
        this.createExercise("sameName","SCALE");
        mockMvc.perform(
                post("/exercicios")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"sameName\",\"type\": \"SCALE\"}")
        ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Data Integrity Violation"));
    }
    @Test
    void shouldNotUpdateInactiveExercise() throws Exception {
        Long id = this.createExercise("name","SCALE");
        this.deactivateExercise(id);
        mockMvc.perform(
                patch("/exercicios/"+ id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Novo Nome\"}")
        ).andExpect(status().isConflict());

    }
    @Test
    void shouldReturnNotFoundWhenExerciseIdDoesNotExist() throws Exception {
        mockMvc.perform(
                        get("/exercicios/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeactivateAlreadyInactiveExercise() throws Exception {
        Long id = this.createExercise("name", "SCALE");
        this.deactivateExercise(id);

        mockMvc.perform(
                        delete("/exercicios/" + id + "/desativar"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotReactivateAlreadyActiveExercise() throws Exception {
        Long id = this.createExercise("name", "SCALE");

        mockMvc.perform(
                        patch("/exercicios/" + id + "/reativar"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenTypeIsInvalid() throws Exception {
        mockMvc.perform(
                        get("/exercicios?type=INVALIDO"))
                .andExpect(status().isBadRequest());
    }


}
