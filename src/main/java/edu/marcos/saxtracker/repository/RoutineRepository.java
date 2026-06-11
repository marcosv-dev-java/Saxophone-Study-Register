package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}
