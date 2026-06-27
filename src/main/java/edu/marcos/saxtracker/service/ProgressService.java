package edu.marcos.saxtracker.service;


import edu.marcos.saxtracker.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProgressService {
    private SessionRepository sessionRepository;


    public ProgressService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

}
