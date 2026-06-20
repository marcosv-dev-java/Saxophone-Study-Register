package edu.marcos.saxtracker.dto.execution;

import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;
import edu.marcos.saxtracker.dto.session.SessionSummary;

public record ExerciseExecutionResponse(
        Long id,
        Double value,
        ExerciseSummary exercise,
        SessionSummary session,
        String notes

) {
}
