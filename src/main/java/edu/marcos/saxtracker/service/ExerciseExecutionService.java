package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.execution.ExerciseExecutionRequest;
import edu.marcos.saxtracker.dto.execution.ExerciseExecutionResponse;
import edu.marcos.saxtracker.dto.exercise.ExerciseResponse;
import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;
import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.*;
import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExerciseExecutionService {
    private final ExerciseExecutionRepository repository;
    private final ExerciseRepository exerciseRepository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;

    public ExerciseExecutionService(ExerciseExecutionRepository repository, ExerciseRepository exerciseRepository, SessionRepository sessionRepository, SessionService sessionService) {
        this.repository = repository;
        this.exerciseRepository = exerciseRepository;
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
    }

    private Exercise findExerciseById(Long id){
        return exerciseRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
    }
    private Session findSessionById(Long id){
        return sessionRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }
    private ExerciseExecution requestToEntity(Long sessionId, ExerciseExecutionRequest request){
        Exercise exercise = findExerciseById(request.exerciseId());
        validateValue(request.value(), exercise);
        Session session = findSessionById(sessionId);
        if (session.getStatus() == SessionStatus.CLOSED) throw new IllegalStateException("Session has been closed");
        if (repository.existsByExercise_IdAndSession_Id(request.exerciseId(), sessionId)) {
            throw new IllegalArgumentException("This exercise has already been registered in this session");
        }
        return new ExerciseExecution(
                request.value(), exercise, session, request.notes()
        );
    }
    private void validateValue(Double value, Exercise exercise){
        if(exercise.getType() == ExerciseType.SCALE){
            if(value <= 0) throw new IllegalArgumentException("The bpm needs to be greater than zero.");
        }
        else if (value < 1 || value > 10) throw new IllegalArgumentException("The value needs to be in range 1-10.");
    }
    private ExerciseExecutionResponse entityToResponse(ExerciseExecution entity){
        SessionSummary sessionSummary = new SessionSummary(entity.getSession().getId(), entity.getSession().getDate());
        return new ExerciseExecutionResponse(
                entity.getId(),
                entity.getValue(),
                new ExerciseSummary(
                        entity.getExercise().getId(),
                        entity.getExercise().getName()
                ),
                sessionSummary,
                entity.getNotes()
        );
    }

    public ExerciseExecutionResponse createExerciseExecution(Long sessionId, ExerciseExecutionRequest request) {
        ExerciseExecution entity = requestToEntity(sessionId, request);
        repository.save(entity);
        sessionService.checkAndCloseSessionIfComplete(sessionId);
        return entityToResponse(entity);
    }
    public List<ExerciseExecutionResponse> getAllExecutions(){
        List<ExerciseExecution> entityList = repository.findAll();
        List<ExerciseExecutionResponse> responses = new ArrayList<>();
        for (ExerciseExecution entity : entityList) {
            responses.add(entityToResponse(entity));
        }
        return responses;
    }
}
