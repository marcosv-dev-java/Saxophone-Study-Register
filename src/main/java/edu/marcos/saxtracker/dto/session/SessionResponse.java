package edu.marcos.saxtracker.dto.session;

import edu.marcos.saxtracker.model.SessionStatus;

import java.time.LocalDate;

public record SessionResponse(
        Long id,
        LocalDate date,
        String notes,
        SessionStatus status,
        Long routineId
) {
}
