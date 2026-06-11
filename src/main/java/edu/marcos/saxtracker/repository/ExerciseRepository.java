package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    public List<Exercise> findByType(ExerciseType type);
}
