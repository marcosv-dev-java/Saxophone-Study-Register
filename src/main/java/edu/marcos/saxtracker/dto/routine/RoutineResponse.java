package edu.marcos.saxtracker.dto.routine;

import java.util.List;

public record RoutineResponse(
        Long id,
        String name,
        String description,
        Boolean active,
        List<ExerciseSummary> exercises

) {
}
