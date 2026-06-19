package edu.marcos.saxtracker.controller;

import com.jayway.jsonpath.JsonPath;
import org.springframework.transaction.annotation.Transactional;
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
public class RoutineControllerTest {
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

    private Long createRoutine(String name, String exerciseIdsJson) throws Exception {
        String response = mockMvc.perform(
                        post("/rotinas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"" + name + "\",\"exerciseIds\":" + exerciseIdsJson + "}")
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private Boolean deactivateRoutine(Long id) throws Exception {
        String response = mockMvc.perform(
                        delete("/rotinas/" + id + "/desativar"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.active");
    }

    @Test
    void shouldAddRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        mockMvc.perform(
                        post("/rotinas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Aquecimento\",\"exerciseIds\":[" + exerciseId + "]}")
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Aquecimento"))
                .andExpect(jsonPath("$.exercises", hasSize(1)));
    }

    @Test
    void shouldNotAddRoutineWithNonexistentExercise() throws Exception {
        mockMvc.perform(
                post("/rotinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Aquecimento\",\"exerciseIds\":[999999]}")
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllRoutines() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        this.createRoutine("Aquecimento", "[" + exerciseId + "]");

        mockMvc.perform(get("/rotinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Aquecimento"));
    }

    @Test
    void shouldGetRoutineById() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");

        mockMvc.perform(get("/rotinas/" + routineId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aquecimento"))
                .andExpect(jsonPath("$.exercises[0].name").value("Escala de Do"));
    }

    @Test
    void shouldReturnNotFoundWhenRoutineIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/rotinas/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateRoutineName() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");

        mockMvc.perform(
                        patch("/rotinas/" + routineId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"Novo Nome\"}")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"))
                .andExpect(jsonPath("$.exercises", hasSize(1)));
    }

    @Test
    void shouldNotUpdateInactiveRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");
        this.deactivateRoutine(routineId);

        mockMvc.perform(
                patch("/rotinas/" + routineId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Novo Nome\"}")
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldDeactivateRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");

        Boolean active = this.deactivateRoutine(routineId);
        Assertions.assertFalse(active);
    }

    @Test
    void shouldNotDeactivateAlreadyInactiveRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");
        this.deactivateRoutine(routineId);

        mockMvc.perform(
                        delete("/rotinas/" + routineId + "/desativar"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReactivateRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");
        this.deactivateRoutine(routineId);

        mockMvc.perform(
                        patch("/rotinas/" + routineId + "/reativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldNotReactivateAlreadyActiveRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", "[" + exerciseId + "]");

        mockMvc.perform(
                        patch("/rotinas/" + routineId + "/reativar"))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotAddRoutineWithBlankName() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        mockMvc.perform(
                        post("/rotinas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"\",\"exerciseIds\":[" + exerciseId + "]}")
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("name: must not be blank")));
    }

}