package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.routine.ExerciseSummary;
import edu.marcos.saxtracker.dto.routine.RoutineRequest;
import edu.marcos.saxtracker.dto.routine.RoutineResponse;
import edu.marcos.saxtracker.dto.routine.RoutineUpdateRequest;
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
    private List<Exercise> idListToExerciseList(List<Long> ids) {
        List<Exercise> exercises = exerciseRepository.findAllById(ids);
        if (exercises.size() != ids.size()) {
            List<Long> foundIds = exercises.stream().map(Exercise::getId).toList();
            List<Long> missingIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException("Exercises not found: " + missingIds);
        }
        return exercises;
    }

    private Routine requestToEntity(RoutineRequest request) {
        List<Exercise> exercises = idListToExerciseList(request.exerciseIds());
        return new Routine(request.name(), exercises,request.description());
    }
    private List<ExerciseSummary> listExercisesToSummary(List<Exercise> exercises){
        return exercises.stream()
                .map(e -> new ExerciseSummary(e.getId(), e.getName()))
                .toList();
    }

    private Routine findRoutineById(Long id){
        return repository.findByIdWithExercises(id)
                .orElseThrow(()->new ResourceNotFoundException("Routine not found with id: " + id));
    }
    private RoutineResponse entityToResponse(Routine routine){
        return new RoutineResponse(routine.getId(),routine.getName(),routine.getDescription(),
                routine.getActive(),
                this.listExercisesToSummary(routine.getExercises()));
    }

    public RoutineResponse createRoutine(RoutineRequest request){
        Routine routine = requestToEntity(request);
        repository.save(routine);
        return entityToResponse(routine);
    }

    public List<RoutineResponse> findAll(){
        List<Routine> allRoutine = repository.findAllWithExercises();
        List<RoutineResponse> response = new ArrayList<>();
        for (Routine r : allRoutine) {
            response.add(entityToResponse(r));
        }
        return response;
    }

    public RoutineResponse findById(Long id){
        return entityToResponse(findRoutineById(id));
    }

    public RoutineResponse updateRoutine(Long id,RoutineUpdateRequest updateRequest){
        Routine routine = findRoutineById(id);
        if(!routine.getActive()) throw new IllegalStateException("Cannot update an inactive routine");
        if(updateRequest.name() != null) routine.setName(updateRequest.name());
        if(updateRequest.description() != null) routine.setDescription(updateRequest.description());
        if(updateRequest.exerciseIds() != null && !updateRequest.exerciseIds().isEmpty())
            routine.setExercises(idListToExerciseList(updateRequest.exerciseIds()));
        repository.save(routine);
        return entityToResponse(routine);
    }

    public RoutineResponse deactivateRoutine(Long id){
        Routine routine = findRoutineById(id);
        if(!routine.getActive())
            throw new IllegalStateException("The active status is already false");
        routine.setActive(false);
        repository.save(routine);
        return entityToResponse(routine);
    }
    public RoutineResponse reactivateRoutine(Long id){
        Routine routine = findRoutineById(id);
        if(routine.getActive())
            throw new IllegalStateException("The active status is already true");
        routine.setActive(true);
        repository.save(routine);
        return entityToResponse(routine);
    }

}
