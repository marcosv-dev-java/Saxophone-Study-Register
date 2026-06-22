package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.assessment.SkillAssessmentRequest;
import edu.marcos.saxtracker.dto.assessment.SkillAssessmentResponse;
import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.Session;
import edu.marcos.saxtracker.model.SessionStatus;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.model.SkillAssessment;
import edu.marcos.saxtracker.repository.SessionRepository;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SkillAssessmentService {
    private final SkillAssessmentRepository repository;
    private final SessionRepository sessionRepository;
    private final SessionService sessionService;

    public SkillAssessmentService(SkillAssessmentRepository repository, SessionRepository sessionRepository, SessionService sessionService) {
        this.repository = repository;
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
    }

    private SkillAssessmentResponse entityToResponse(SkillAssessment entity) {
        return new SkillAssessmentResponse(
                entity.getId(),
                entity.getSkill(),
                new SessionSummary(entity.getSession().getId()
                ,entity.getSession().getDate()),
                entity.getValue()
        );

    }
    @Transactional
    public List<SkillAssessmentResponse> evaluateSession(Long sessionId, List<SkillAssessmentRequest> requests){
        Set<Skill> uniqueSkills = new HashSet<>();
        List<SkillAssessmentResponse> responses = new ArrayList<>();
        Session session = sessionRepository.findById(sessionId).orElseThrow(
                () -> new ResourceNotFoundException("Session not found with id " + sessionId)
        );
        if (session.getStatus() == SessionStatus.CLOSED)
            throw new IllegalStateException("Session is already closed");
        for (SkillAssessmentRequest assessment : requests) {
            uniqueSkills.add(assessment.skill());
        }
        if (uniqueSkills.size() != requests.size()) {
            int diferences = requests.size() - uniqueSkills.size();
            throw new IllegalArgumentException(diferences + " assessment is duplicated ");
        }
        if (requests.size() != Skill.values().length) {
            throw new IllegalArgumentException("All skills must be assessed");
        }
        for (SkillAssessmentRequest assessment : requests) {
            if (assessment.value() <= 0 || assessment.value() > 10)
                throw new IllegalArgumentException("Value must be between 0 and 10");
            if (repository.existsBySkillAndSession_Id(assessment.skill(), sessionId))
                throw new IllegalArgumentException("Skill assessment to "+ assessment.skill().toString() +" already exists in this session");
            SkillAssessment skill = new SkillAssessment(
                    assessment.skill(),
                    session,
                    assessment.value()
            );
            repository.save(skill);
            responses.add(entityToResponse(skill));
        }
        sessionService.checkAndCloseSessionIfComplete(sessionId);
        return responses;
    }
}
