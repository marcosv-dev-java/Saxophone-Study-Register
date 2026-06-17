package edu.marcos.saxtracker.dto.routine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RoutineRequest(
        @NotBlank String name,
        String description,
        @NotEmpty List<Long> exerciseIds
) {
}
