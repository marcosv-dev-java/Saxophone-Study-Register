package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.ExerciseExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExerciseExecutionRepository extends JpaRepository<ExerciseExecution, Long> {
    boolean existsByExercise_IdAndSession_Id(Long exerciseId, Long sessionId);
    // Verifica se já existe o exercício na sessão
    long countBySession_Id(Long sessionId);
    @Query("""
    SELECT AVG(e.value)
    FROM ExerciseExecution e
    JOIN e.session s
    WHERE e.exercise = :exercise
      AND s.date BETWEEN :start AND :end
""")
    Double findAverageByExerciseAndPeriod(
            @Param("exercise") Exercise exercise,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    @Query("""
    SELECT e.exercise
    FROM ExerciseExecution e
    JOIN e.session s
    WHERE s.date BETWEEN :start AND :end
    GROUP BY e.exercise
    ORDER BY COUNT(e) DESC
    LIMIT 1
""")
    // Query que retorna a lista dos exercícios mais praticados, de forma decrescente(do mais praticado até o menos)
    Optional<Exercise> findMostPracticedExercise(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    @Query("""
            SELECT AVG(e.value)
            FROM ExerciseExecution e
            JOIN e.session s
            WHERE e.exercise.type = edu.marcos.saxtracker.model.ExerciseType.SCALE
            AND s.date BETWEEN :start AND :end
            """)
    Double averageWeeklyByBpm(@Param("start") LocalDate start,
                              @Param("end") LocalDate end);
    @Query("""
    SELECT AVG(e.value)
    FROM ExerciseExecution e
    JOIN e.session s
    WHERE e.exercise.type <> edu.marcos.saxtracker.model.ExerciseType.SCALE
    AND s.date BETWEEN :start AND :end
""")
    Double averageWeeklyByValue(@Param("start") LocalDate start,
                                @Param("end") LocalDate end);

}
