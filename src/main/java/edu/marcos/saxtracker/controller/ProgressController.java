package edu.marcos.saxtracker.controller;
import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.dto.progress.SkillWeekComparisonResponse;
import edu.marcos.saxtracker.dto.progress.WeekSummaryProgress;
import edu.marcos.saxtracker.dto.progress.WeeklyProgressResponse;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.service.ProgressService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/progresso")
public class ProgressController {
    private final ProgressService service;

    public ProgressController(ProgressService service) {
        this.service = service;
    }

    @GetMapping("/habilidades")
    public ResponseEntity<List<SkillProgressResponse>> getSkillProgressInWeek(@Parameter(description = "Get skill progress in the requested week", example = "2026-W15",
                                                                schema = @Schema(description = "Format: YYYY-Www")) @RequestParam String week) {

        return ResponseEntity.ok().body(service.skillSummaryInWeek(week));
    }
    @GetMapping("/habilidades/{skill}")
    public ResponseEntity<SkillWeekComparisonResponse> compareWeekWithPreviousWeekBySkill(@Parameter(description = "Compare requested week with the previous week using the requested skill"
                                                                                              , example = "TIMBRE")
                                                                                            @PathVariable Skill skill,
                                                                                          @Parameter(description = "The week requested for comparison with the previous week", example = "2026-W15")
                                                                                          @RequestParam String week){
        return ResponseEntity.ok().body(service.compareWeekBySkill(skill, week));
    }

    @GetMapping("/exercicios/{id}/historico")
    public ResponseEntity<List<WeeklyProgressResponse>> getExerciseEvolutionInTheRange(@Parameter(description = "Exercise id")
                                                      @PathVariable Long id, @RequestParam
                                                  @Parameter(description = "Parameter to compare the current week with previous weeks within the requested range.")
                                                  Integer weekPeriod) {
        return ResponseEntity.ok().body(service.getExerciseEvolutionInPeriod(id, weekPeriod));
    }
    @GetMapping("/exercicios/{id}")
    public ResponseEntity<WeeklyProgressResponse> getExerciseEvolutionInTheRange(@Parameter(description = "Exercise id")
                                                                                 @PathVariable Long id) {
        return ResponseEntity.ok().body(service.getExerciseEvolutionInActualWeek(id));
    }

    @GetMapping("/resumo")
    public ResponseEntity<WeekSummaryProgress> getWeekSummary(@RequestParam(required = false)
                                                              @Parameter(description = "Target week to get a summary", example = "2026-W21")
                                                              String week){
        if(week != null && !week.isBlank()){
            return ResponseEntity.ok().body(service.getWeekSummary(week));
        }
        return ResponseEntity.ok().body(service.getWeekSummaryInActualWeek());
    }
}
