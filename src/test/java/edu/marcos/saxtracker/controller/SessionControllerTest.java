package edu.marcos.saxtracker.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SessionControllerTest {
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

    private Long createRoutine(String name, Long exerciseId) throws Exception {
        String response = mockMvc.perform(
                        post("/rotinas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"name\":\"" + name + "\",\"exerciseIds\":[" + exerciseId + "]}")
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private void deactivateRoutine(Long id) throws Exception {
        mockMvc.perform(delete("/rotinas/" + id + "/desativar"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateSession() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);

        mockMvc.perform(
                        post("/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"routineId\":" + routineId + ",\"notes\":\"Boa sessao\"}")
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.notes").value("Boa sessao"));
    }

    @Test
    void shouldNotCreateSessionWithInactiveRoutine() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);
        this.deactivateRoutine(routineId);

        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":" + routineId + ",\"notes\":\"Boa sessao\"}")
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldNotCreateSessionWithNonexistentRoutine() throws Exception {
        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":999999,\"notes\":\"Boa sessao\"}")
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldNotCreateTwoSessionsOnSameDate() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);

        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":" + routineId + ",\"notes\":\"Primeira\"}")
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":" + routineId + ",\"notes\":\"Segunda\"}")
        ).andExpect(status().isConflict());
    }

    @Test
    void shouldGetAllSessions() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);

        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":" + routineId + ",\"notes\":\"Boa sessao\"}")
        ).andExpect(status().isCreated());

        mockMvc.perform(get("/sessoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldGetSessionById() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);

        String response = mockMvc.perform(
                        post("/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"routineId\":" + routineId + ",\"notes\":\"Boa sessao\"}")
                ).andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long sessionId = ((Number) JsonPath.read(response, "$.id")).longValue();

        mockMvc.perform(get("/sessoes/" + sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Boa sessao"));
    }

    @Test
    void shouldReturnNotFoundWhenSessionIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/sessoes/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindSessionByDate() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Aquecimento", exerciseId);

        mockMvc.perform(
                post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\":" + routineId + ",\"notes\":\"Boa sessao\"}")
        ).andExpect(status().isCreated());

        String today = java.time.LocalDate.now().toString();

        mockMvc.perform(get("/sessoes/data/" + today))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Boa sessao"));
    }

    @Test
    void shouldReturnNotFoundWhenNoSessionOnDate() throws Exception {
        mockMvc.perform(get("/sessoes/data/2020-01-01"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/sessoes/data/14-04-1999"))
                .andExpect(status().isBadRequest());
    }
}