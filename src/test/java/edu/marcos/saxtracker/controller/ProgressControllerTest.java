package edu.marcos.saxtracker.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProgressControllerTest {

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

    private Long createSession(Long routineId) throws Exception {
        String response = mockMvc.perform(
                        post("/sessoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"routineId\":" + routineId + ",\"notes\":\"Treino de progresso\"}")
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return ((Number) JsonPath.read(response, "$.id")).longValue();
    }

    private void addExecution(Long sessionId, Long exerciseId, int value) throws Exception {
        mockMvc.perform(
                post("/sessoes/" + sessionId + "/execucoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":" + value + ",\"exerciseId\": " + exerciseId + "}")
        ).andExpect(status().isCreated());
    }

    private void evaluateSession(Long sessionId) throws Exception {
        String body = "[" +
                "{\"skill\":\"TIMBRE\",\"value\":8}," +
                "{\"skill\":\"TUNING\",\"value\":7}," +
                "{\"skill\":\"ARTICULATION\",\"value\":9}," +
                "{\"skill\":\"BREATHING\",\"value\":6}," +
                "{\"skill\":\"READING\",\"value\":7}" +
                "]";
        mockMvc.perform(post("/sessoes/" + sessionId + "/avaliacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    private void criarDadosDeTreinoCompletos() throws Exception {
        Long exerciseId = this.createExercise("Escala de Do", "SCALE");
        Long routineId = this.createRoutine("Rotina Diaria", exerciseId);
        Long sessionId = this.createSession(routineId);
        this.addExecution(sessionId, exerciseId, 120);
        this.evaluateSession(sessionId);
    }
    @Test
    void shouldGetSkillSummaryInWeek_WhenNoSkillProvided() throws Exception {
        this.criarDadosDeTreinoCompletos();
        String currentWeek = "2026-W27";

        mockMvc.perform(get("/progresso/habilidades/" + currentWeek))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].skill", hasItem("TIMBRE")));
    }

    @Test
    void shouldCompareWeekBySkill_WhenSkillIsProvided() throws Exception {
        this.criarDadosDeTreinoCompletos();
        String currentWeek = "2026-W27";

        mockMvc.perform(get("/progresso/habilidades/" + currentWeek)
                        .param("skill", "TIMBRE"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_WhenSkillEnumIsInvalid() throws Exception {
        String currentWeek = "2026-W27";

        mockMvc.perform(get("/progresso/habilidades/" + currentWeek)
                        .param("skill", "HABILIDADE_INVALIDA"))
                .andExpect(status().isBadRequest()); // O Spring Boot barra automaticamente pelo Enum
    }
    @Test
    void shouldGetExerciseEvolutionInActualWeek_WhenNoPeriodProvided() throws Exception {
        Long exerciseId = this.createExercise("Escala de Sol", "SCALE");
        Long routineId = this.createRoutine("Rotina B", exerciseId);
        Long sessionId = this.createSession(routineId);
        this.addExecution(sessionId, exerciseId, 100);

        mockMvc.perform(get("/progresso/exercicios/" + exerciseId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetExerciseEvolutionInPeriod_WhenPeriodIsProvided() throws Exception {
        Long exerciseId = this.createExercise("Escala de Sol", "SCALE");
        Long routineId = this.createRoutine("Rotina B", exerciseId);
        Long sessionId = this.createSession(routineId);
        this.addExecution(sessionId, exerciseId, 100);

        mockMvc.perform(get("/progresso/exercicios/" + exerciseId)
                        .param("weekPeriod", "4"))
                .andExpect(status().isOk());
    }
    @Test
    void shouldReturnNotFound_WhenExerciseDoesNotExist() throws Exception {
        mockMvc.perform(get("/progresso/exercicios/999999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void shouldGetWeekSummaryInActualWeek_WhenNoWeekProvided() throws Exception {
        this.criarDadosDeTreinoCompletos();

        mockMvc.perform(get("/progresso/resumo"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetWeekSummary_WhenSpecificWeekIsProvided() throws Exception {
        this.criarDadosDeTreinoCompletos();
        String targetWeek = "2026-W27";

        mockMvc.perform(get("/progresso/resumo")
                        .param("week", targetWeek))
                .andExpect(status().isOk());
    }
}