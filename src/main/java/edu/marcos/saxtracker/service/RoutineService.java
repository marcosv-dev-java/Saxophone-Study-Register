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
        return new Routine(request.name(), exercises,request.description());
    }
    private List<ExerciseSummary> listIdExercisesToSummary(List<Long> exercisesId){
        List<ExerciseSummary> exercisesSummary = new ArrayList<>();
        List<Exercise> exercises = exerciseRepository.findAllById(exercisesId);
        for (Exercise exercise : exercises) {
            exercisesSummary.add(new ExerciseSummary(exercise.getId(), exercise.getName()));
        }
        if(exercisesId.size() != exercises.size()) {
            List<Long> foundIds = exercises.stream().map(Exercise::getId).toList();
            List<Long> missingIds = exercisesId.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Exercises not found: " + missingIds);
        }
        return exercisesSummary;
    }

    public RoutineResponse createRoutine(RoutineRequest request){
        Routine routine = requestToEntity(request);
        repository.save(routine);
        return new RoutineResponse(routine.getId(),routine.getName(),routine.getDescription(),
                routine.getActive(),
                this.listIdExercisesToSummary(request.exerciseIds()));

    }

}
