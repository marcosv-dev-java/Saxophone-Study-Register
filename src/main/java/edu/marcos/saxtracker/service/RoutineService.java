package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.routine.ExerciseSummary;
import edu.marcos.saxtracker.dto.routine.RoutineRequest;
import edu.marcos.saxtracker.dto.routine.RoutineResponse;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.Routine;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutineService {
    private final RoutineRepository repository;
    private final ExerciseRepository exerciseRepository;

    public RoutineService(RoutineRepository repository, ExerciseRepository exerciseRepository) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
    }

    private Routine requestToEntity(RoutineRequest request) {
        List<Exercise> exercises = exerciseRepository.findAllById(request.exerciseIds());
        if (exercises.size() != request.exerciseIds().size()) {
            List<Long> foundIds = exercises.stream().map(Exercise::getId).toList();
            List<Long> missingIds = request.exerciseIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Exercises not found: " + missingIds);
        }
        return new Routine(request.name(), exercises,request.description());
    }
    private List<ExerciseSummary> listExercisesToSummary(List<Exercise> exercises){
        return exercises.stream()
                .map(e -> new ExerciseSummary(e.getId(), e.getName()))
                .toList();
    }

    public RoutineResponse createRoutine(RoutineRequest request){
        Routine routine = requestToEntity(request);
        repository.save(routine);
        return new RoutineResponse(routine.getId(),routine.getName(),routine.getDescription(),
                routine.getActive(),
                this.listExercisesToSummary(routine.getExercises()));

    }

}
