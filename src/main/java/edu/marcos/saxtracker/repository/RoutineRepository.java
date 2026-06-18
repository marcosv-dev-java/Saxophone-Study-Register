package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Query("SELECT DISTINCT r FROM Routine r JOIN FETCH r.exercises")
    List<Routine> findAllWithExercises();
}
