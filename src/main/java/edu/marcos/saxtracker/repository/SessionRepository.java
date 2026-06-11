package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
