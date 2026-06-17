package edu.marcos.saxtracker.dto.exercise;

import edu.marcos.saxtracker.model.ExerciseType;

public record ExerciseResponse(
        Long id, String name,
        String description,
        ExerciseType type,
        Boolean active
) {
}
