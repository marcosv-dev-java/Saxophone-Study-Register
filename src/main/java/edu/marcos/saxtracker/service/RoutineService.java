package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.repository.RoutineRepository;
import org.springframework.stereotype.Service;

@Service
public class RoutineService {
    private final RoutineRepository repository;

    public RoutineService(RoutineRepository repository) {
        this.repository = repository;
    }


}
