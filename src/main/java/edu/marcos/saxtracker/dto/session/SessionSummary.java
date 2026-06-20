package edu.marcos.saxtracker.dto.session;

import java.time.LocalDate;

public record SessionSummary(
        Long id, LocalDate date
) {
}
