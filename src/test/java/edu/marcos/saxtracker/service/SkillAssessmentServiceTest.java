package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.assessment.SkillAssessmentRequest;
import edu.marcos.saxtracker.dto.assessment.SkillAssessmentResponse;
import edu.marcos.saxtracker.dto.assessment.SkillAssessmentResponseTest;
import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.model.Session;
import edu.marcos.saxtracker.model.SessionStatus;
import edu.marcos.saxtracker.model.Skill;
import edu.marcos.saxtracker.repository.SessionRepository;
import edu.marcos.saxtracker.repository.SkillAssessmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillAssessmentServiceTest {
    private Long sessionId;
    private Session sessionMock;
    @Mock
    private SkillAssessmentRepository repository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionService sessionService;
    @InjectMocks
    private SkillAssessmentService service;

    @BeforeEach
    void setUp() {
        sessionId = 1L;
        sessionMock = Mockito.mock(Session.class);
        when(sessionMock.getDate()).thenReturn(LocalDate.now());
        when(sessionMock.getId()).thenReturn(sessionId);
    }

    @Test
    void shouldEvaluteSessionAndReturnSkillAssessment() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(repository.existsBySkillAndSession_Id(any(),any())).thenReturn(false);
        SessionSummary sessionSummary = new SessionSummary(sessionMock.getId(), sessionMock.getDate());
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 5)
        );
        List<SkillAssessmentResponseTest> expected = new ArrayList<>();
        for(SkillAssessmentRequest request : requests){
            expected.add(new SkillAssessmentResponseTest(
                    request.skill(),
                    sessionSummary,
                    request.value()));
        }
        List<SkillAssessmentResponse> responses = service.evaluateSession(sessionId, requests);
        List<SkillAssessmentResponseTest> actual = new ArrayList<>();
        for(SkillAssessmentResponse response : responses){
            actual.add(new SkillAssessmentResponseTest(
                    response.skill(),
                    response.session(),
                    response.value()));
        }
        assertEquals(expected, actual);
        assertEquals(responses.size(), expected.size());
        verify(repository, times(Skill.values().length)).save(any());
        verify(sessionService).checkAndCloseSessionIfComplete(sessionId);

    }

}
