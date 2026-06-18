package edu.marcos.saxtracker.dto.routine;



import java.util.List;

public record RoutineUpdateRequest(
        String name,
        String description,
        List<Long> exerciseIds
) {
}
