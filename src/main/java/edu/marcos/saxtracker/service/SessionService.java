package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.session.SessionRequest;
import edu.marcos.saxtracker.dto.session.SessionResponse;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Routine;
import edu.marcos.saxtracker.model.Session;
import edu.marcos.saxtracker.repository.RoutineRepository;
import edu.marcos.saxtracker.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepository repository;
    private final RoutineRepository routineRepository;

    public SessionService(SessionRepository repository, RoutineRepository routineRepository) {
        this.repository = repository;
        this.routineRepository = routineRepository;
    }
    private Routine findRoutineById(Long id){
       return routineRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Routine not found with id: " + id));
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
}
