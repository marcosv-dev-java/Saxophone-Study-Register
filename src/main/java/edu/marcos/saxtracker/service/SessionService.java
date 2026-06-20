package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private SessionRepository repository;

    public SessionService(SessionRepository repository) {
        this.repository = repository;
    }
}
