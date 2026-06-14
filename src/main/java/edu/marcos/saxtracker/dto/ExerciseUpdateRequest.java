package edu.marcos.saxtracker.dto;

import edu.marcos.saxtracker.model.ExerciseType;

public record ExerciseUpdateRequest(
        String name,
        String description,
        ExerciseType type
) {
}
