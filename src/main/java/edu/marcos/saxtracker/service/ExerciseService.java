package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExerciseService {
    private ExerciseRepository repository;


    public ExerciseService(ExerciseRepository repository) {
        this.repository = repository;
    }

}
