package edu.marcos.saxtracker.dto.assessment;

import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.model.Skill;

public record SkillAssessmentResponseTest(
        Skill skill,
        SessionSummary session,
        Integer value
) {
}
