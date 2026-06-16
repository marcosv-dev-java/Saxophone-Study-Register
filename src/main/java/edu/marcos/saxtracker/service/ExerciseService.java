package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.ExerciseRequest;
import edu.marcos.saxtracker.dto.ExerciseResponse;
import edu.marcos.saxtracker.dto.ExerciseUpdateRequest;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Exercise;
import edu.marcos.saxtracker.model.ExerciseType;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository repository;


    public ExerciseService(ExerciseRepository repository) {
        this.repository = repository;
    }

    public ExerciseResponse save(ExerciseRequest request){
        Exercise exercise = requestToEntity(request);
        repository.save(exercise);
        return entityToResponse(exercise);
    }

    private Exercise requestToEntity(ExerciseRequest request){
        return new Exercise(request.name(), request.description(), request.type());
    }
    public ExerciseResponse findById(Long id){
        Exercise exercise = findExerciseById(id);
        return entityToResponse(exercise);
    }
    private ExerciseResponse entityToResponse(Exercise exercise){
        return new ExerciseResponse(exercise.getId(), exercise.getName(), exercise.getDescription(), exercise.getType(),exercise.getActive());
    }
    private List<ExerciseResponse> entityListToResponseList(List<Exercise> exercises){
        List<ExerciseResponse> responses = new ArrayList<>();
        for(Exercise exercise : exercises){
            responses.add(entityToResponse(exercise));
        }
        return responses;
    }

    public List<ExerciseResponse> findAll(){
        return entityListToResponseList(repository.findAll());
    }
    public List<ExerciseResponse> filterByType(ExerciseType type){
        return entityListToResponseList(repository.findByType(type));
    }
    private Exercise findExerciseById(Long id) {
        return repository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id " + id));
    }


    public ExerciseResponse update(Long id,ExerciseUpdateRequest request){
        Exercise exercise = findExerciseById(id);
        if(!exercise.getActive()) throw new IllegalStateException("Cannot update an inactive exercise");
        if (request.name() != null) exercise.setName(request.name());
        if (request.description() != null) exercise.setDescription(request.description());
        if (request.type() != null) exercise.setType(request.type());
        repository.save(exercise);
        return entityToResponse(exercise);
    }
    public ExerciseResponse reactivate(Long id){
        Exercise exercise = findExerciseById(id);
        if(exercise.getActive()) throw new IllegalStateException("The exercise is already active.");
        exercise.setActive(true);
        repository.save(exercise);
        return entityToResponse(exercise);

    }

    public void hardDelete(Long id){
        Exercise exercise = findExerciseById(id);
        repository.delete(exercise);
    }
    public ExerciseResponse softDelete(Long id){
        Exercise exercise = findExerciseById(id);
        if(!exercise.getActive())
            throw new IllegalStateException("The active status is already false");
        exercise.setActive(false);
        repository.save(exercise);
        return entityToResponse(exercise);
    }


}
