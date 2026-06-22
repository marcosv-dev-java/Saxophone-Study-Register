package edu.marcos.saxtracker.dto.assessment;

import edu.marcos.saxtracker.model.Skill;
import jakarta.validation.constraints.NotNull;

public record SkillAssessmentRequest(
        @NotNull Skill skill,
        @NotNull Integer value
) {
}
