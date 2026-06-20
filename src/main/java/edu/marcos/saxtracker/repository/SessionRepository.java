package edu.marcos.saxtracker.repository;

import edu.marcos.saxtracker.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByDate(LocalDate date);
}
