package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.ExerciseRequest;
import edu.marcos.saxtracker.dto.ExerciseResponse;
import edu.marcos.saxtracker.exceptions.ExerciseNotFoundException;
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
        Exercise exercise = repository.findById(id).
                orElseThrow(ExerciseNotFoundException::new);
        return entityToResponse(exercise);
    }
    private ExerciseResponse entityToResponse(Exercise exercise){
        return new ExerciseResponse(exercise.getId(), exercise.getName(), exercise.getDescription(), exercise.getType());
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


}
