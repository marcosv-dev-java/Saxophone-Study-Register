package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Query("SELECT DISTINCT r FROM Routine r JOIN FETCH r.exercises")
    List<Routine> findAllWithExercises();


    @Query("SELECT DISTINCT r FROM Routine r JOIN FETCH r.exercises WHERE r.id = :id")
    Optional<Routine> findByIdWithExercises(@Param("id") Long id);
}
