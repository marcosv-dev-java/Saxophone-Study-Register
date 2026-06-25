package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.assessment.SkillAssessmentRequest;
import edu.marcos.saxtracker.dto.assessment.SkillAssessmentResponse;
import edu.marcos.saxtracker.dto.assessment.SkillAssessmentResponseTest;
import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        lenient().when(sessionMock.getDate()).thenReturn(LocalDate.now());
        lenient().when(sessionMock.getId()).thenReturn(sessionId);
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
    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 5)
        );
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("Session not found with id " + sessionId,exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenSessionIsClosed() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.CLOSED);
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 5)
        );
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("Session is already closed",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenValueIsInvalid() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 11),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 0)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("Value must be between 1 and 10",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenAssessmentAlreadyExistsInSession() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(repository.existsBySkillAndSession_Id(any(),any())).thenReturn(true);
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 5)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("Skill assessment to "+ requests.getFirst().skill().toString() +" already exists in this session",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenAssessmentIsDuplicated(){
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(repository.existsBySkillAndSession_Id(any(),any())).thenReturn(false);
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.ARTICULATION, 5),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.BREATHING, 4),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4),
                new SkillAssessmentRequest(Skill.TUNING, 5)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("2 assessment is duplicated ",exception.getMessage());
    }
    @Test
    void shouldThrowExceptionWhenIsMissingSkill(){
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(repository.existsBySkillAndSession_Id(any(),any())).thenReturn(false);
        List<SkillAssessmentRequest> requests = List.of(
                new SkillAssessmentRequest(Skill.ARTICULATION, 1),
                new SkillAssessmentRequest(Skill.BREATHING, 2),
                new SkillAssessmentRequest(Skill.READING, 3),
                new SkillAssessmentRequest(Skill.TIMBRE, 4)
        );
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.evaluateSession(sessionId,requests));
        assertEquals("All skills must be assessed",exception.getMessage());
    }


}
