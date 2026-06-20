package edu.marcos.saxtracker.dto.routine;

import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;

import java.util.List;

public record RoutineResponse(
        Long id,
        String name,
        String description,
        Boolean active,
        List<ExerciseSummary> exercises

) {
}
