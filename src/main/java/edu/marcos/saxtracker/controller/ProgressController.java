package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.progress.SkillProgressResponse;
import edu.marcos.saxtracker.dto.progress.SkillWeekComparisonResponse;
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
    public ResponseEntity<?> getSkillProgressInWeek(@Parameter(description = "Target week for the summary", example = "2026-W15",
                                                                                      schema = @Schema(description = "Format: YYYY-Www")) @PathVariable String week,
                                                                                                           @RequestParam(required = false)
                                                                              @Parameter(description = "Optional skill to compare with previous week", example = "SCALE")
                                                                              Skill skill) {
        if(skill != null){
            return ResponseEntity.ok().body(service.compareWeekBySkill(skill,week));
        }
        return ResponseEntity.ok().body(service.skillSummaryInWeek(week));
    }
}
