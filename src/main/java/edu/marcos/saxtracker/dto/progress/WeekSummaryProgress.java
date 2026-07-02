package edu.marcos.saxtracker.dto.progress;
import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

public record WeekSummaryProgress(
        @Schema(description = "Most practiced exercise in the week. Null if no executions were recorded.")
        @Nullable ExerciseSummary exerciseMostPracticed,
        @Schema(description = "Average BPM of scale exercises in the week. Null if no scale executions were recorded.")
        @Nullable Double bpmAverage,
        @Schema(description = "Average note (1-10) of non-scale exercises in the week. Null if no non-scale executions were recorded.")
        @Nullable Double nonScaleAverage
) {
}
