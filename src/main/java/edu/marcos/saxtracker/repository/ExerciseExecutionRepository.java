package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.ExerciseExecution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseExecutionRepository extends JpaRepository<ExerciseExecution, Long> {
    boolean existsByExercise_IdAndSession_Id(Long exerciseId, Long sessionId);
    // Verifica se já existe o exercício na sessão
    long countBySession_Id(Long sessionId);
}
