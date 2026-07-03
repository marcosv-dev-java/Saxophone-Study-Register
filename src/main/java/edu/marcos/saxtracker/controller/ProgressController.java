package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.dto.progress.SkillWeekComparisonResponse;
import edu.marcos.saxtracker.dto.progress.WeekSummaryProgress;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.service.ProgressService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @GetMapping("/habilidades/{week}")
    public ResponseEntity<?> getSkillProgressInWeek(@Parameter(description = "Target week for the skill progress summary", example = "2026-W15",
                                                                                      schema = @Schema(description = "Format: YYYY-Www")) @PathVariable String week,
                                                                                                           @RequestParam(required = false)
                                                                              @Parameter(description = "Optional skill to compare with previous week", example = "TIMBRE")
                                                                              Skill skill) {
        if(skill != null){
            return ResponseEntity.ok().body(service.compareWeekBySkill(skill,week));
        }
        return ResponseEntity.ok().body(service.skillSummaryInWeek(week));
    }
    @GetMapping("/exercicios/{id}")
    public ResponseEntity<?> getExerciseEvolution(@Parameter(description = "Exercise id")
                                                  @PathVariable Long id,
                                                  @RequestParam(required = false)
                                                  @Parameter(description = "Optional parameter to compare the current week with previous weeks within the requested range.")
                                                  Integer weekPeriod){
        if(weekPeriod != null) {
            return ResponseEntity.ok().body(service.getExerciseEvolutionInPeriod(id, weekPeriod));
        }
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
