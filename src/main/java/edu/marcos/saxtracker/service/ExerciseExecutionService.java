package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseExecutionService {
    private final ExerciseExecutionRepository repository;
    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;

    public ExerciseExecutionService(ExerciseExecutionRepository repository, ExerciseRepository exerciseRepository, SessionRepository sessionRepository) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
        this.sessionRepository = sessionRepository;
    }
}
