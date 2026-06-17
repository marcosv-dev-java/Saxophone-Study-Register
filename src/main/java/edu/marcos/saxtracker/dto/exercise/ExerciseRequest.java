package edu.marcos.saxtracker.dto.exercise;

import edu.marcos.saxtracker.model.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseRequest(
        @NotBlank String name,
        String description,
        @NotNull ExerciseType type) {
}
