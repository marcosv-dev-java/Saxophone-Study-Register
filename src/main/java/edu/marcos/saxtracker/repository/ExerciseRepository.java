package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
