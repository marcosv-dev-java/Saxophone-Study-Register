package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.session.SessionRequest;
import edu.marcos.saxtracker.dto.session.SessionResponse;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Routine;
import edu.marcos.saxtracker.model.Session;
import edu.marcos.saxtracker.model.SessionStatus;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.RoutineRepository;
import edu.marcos.saxtracker.repository.SessionRepository;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {
    private final SessionRepository repository;
    private final RoutineRepository routineRepository;
    private final ExerciseExecutionRepository executionRepository;
    private final SkillAssessmentRepository assessmentRepository;

    public SessionService(SessionRepository repository, RoutineRepository routineRepository, ExerciseExecutionRepository exerciseExecutionRepository, SkillAssessmentRepository skillAssessmentRepository) {
        this.repository = repository;
        this.routineRepository = routineRepository;
        this.executionRepository = exerciseExecutionRepository;
        this.assessmentRepository = skillAssessmentRepository;
    }

    private Routine findRoutineById(Long id){
       return routineRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Routine not found with id: " + id));
    }
    private Session findSessionById(Long id){
        return repository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Session not found with id: " + id));
    }

    private SessionResponse entityToResponse(Session session){
        return new SessionResponse(session.getId(),session.getDate(),session.getNotes(),
                session.getStatus(),session.getRoutine().getId());
    }
    private Session requestToEntity(SessionRequest request){
        Routine routine = findRoutineById(request.routineId());
        if(!routine.getActive()) throw new IllegalStateException("Routine is not active.");
        return new Session(routine, request.notes());
    }

    public SessionResponse createSession(SessionRequest request) {
        Session session = requestToEntity(request);
        repository.save(session);
        return entityToResponse(session);
    }

    public SessionResponse findById(Long id){
        return entityToResponse(findSessionById(id));
    }
    public SessionResponse findByDate(LocalDate date){
        return entityToResponse(repository.findByDate(date).
                orElseThrow(() -> new ResourceNotFoundException("No sessions were created on that date.")));
    }
    public List<SessionResponse> findAll(){
        List<Session> sessions = repository.findAll();
        List<SessionResponse> responses = new ArrayList<>();
        for(Session session : sessions){
            responses.add(entityToResponse(session));
        }
        return responses;
    }
    public void checkAndCloseSessionIfComplete(Long sessionId) {
        Session session = findSessionById(sessionId);

        long executionsCount = executionRepository.countBySession_Id(sessionId);
        long expectedExecutions = session.getRoutine().getExercises().size();

        long assessmentsCount = assessmentRepository.countBySession_Id(sessionId);
        long expectedAssessments = Skill.values().length;

        if (executionsCount == expectedExecutions && assessmentsCount == expectedAssessments) {
            session.setStatus(SessionStatus.CLOSED);
            repository.save(session);
        }
    }


}
