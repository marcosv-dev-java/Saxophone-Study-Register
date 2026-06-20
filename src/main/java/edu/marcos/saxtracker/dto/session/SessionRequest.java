package edu.marcos.saxtracker.dto.session;
import jakarta.validation.constraints.NotNull;

public record SessionRequest(
        String notes,
        @NotNull Long routineId
        ) {
}
