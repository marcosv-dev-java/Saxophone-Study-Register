package edu.marcos.saxtracker.dto.execution;

import jakarta.validation.constraints.NotNull;

public record ExerciseExecutionRequest(
        @NotNull Double value,
        @NotNull Long exerciseId,
        String notes
) {
}
