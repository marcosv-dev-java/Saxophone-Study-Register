package edu.marcos.saxtracker.service;

import edu.marcos.saxtracker.dto.execution.ExerciseExecutionRequest;
import edu.marcos.saxtracker.dto.execution.ExerciseExecutionResponse;
import edu.marcos.saxtracker.dto.exercise.ExerciseSummary;
import edu.marcos.saxtracker.dto.session.SessionSummary;
import edu.marcos.saxtracker.exceptions.ResourceNotFoundException;
import edu.marcos.saxtracker.model.*;
import edu.marcos.saxtracker.repository.ExerciseExecutionRepository;
import edu.marcos.saxtracker.repository.ExerciseRepository;
import edu.marcos.saxtracker.repository.SessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseExecutionServiceTest {
    private Long exerciseId;
    private Long sessionId;
    private Exercise exerciseMock;
    private Session sessionMock;
    @Mock
    private ExerciseExecutionRepository repository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SessionService sessionService;

    @InjectMocks
    private ExerciseExecutionService service;

    @BeforeEach
    void setUp() {
        exerciseId = 1L;
        sessionId = 1L;
        exerciseMock = Mockito.mock(Exercise.class);
        sessionMock = Mockito.mock(Session.class);
        lenient().when(exerciseMock.getId()).thenReturn(exerciseId);
        lenient().when(sessionMock.getId()).thenReturn(sessionId);
    }

    @Test
    void shouldCreateExerciseExecution(){
        when(exerciseMock.getType()).thenReturn(ExerciseType.SCALE);
        when(exerciseMock.getName()).thenReturn("Escala Maior");
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(sessionMock.getDate()).thenReturn(LocalDate.now());
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(sessionRepository.findById(any())).thenReturn(Optional.of(sessionMock));
        when(repository.existsByExercise_IdAndSession_Id(any(), any())).thenReturn(false);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(70.0,exerciseId, "idk what i will say here");
        ExerciseExecutionResponse response = service.createExerciseExecution(sessionId,request);
        Assertions.assertEquals(request.value(), response.value());
        Assertions.assertEquals(new ExerciseSummary(exerciseMock.getId(),exerciseMock.getName()),response.exercise());
        Assertions.assertEquals(request.notes(), response.notes());
        Assertions.assertEquals(new SessionSummary(sessionMock.getId(),sessionMock.getDate()),response.session());
        verify(sessionService).checkAndCloseSessionIfComplete(sessionId);
        verify(repository).save(any());
    }
    @Test
    void shouldThrowWhenExerciseNotFound(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.empty());
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(70.0, exerciseId, "nota");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.createExerciseExecution(sessionId, request));
        assertEquals("Exercise not found",exception.getMessage());
    }
    @Test
    void shouldThrowWhenIllegalArgumentByExerciseValueScale(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.SCALE);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(0.0,exerciseId,"scale");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createExerciseExecution(sessionId,request));
        assertEquals("The bpm needs to be greater than zero.",exception.getMessage());

    }
    @Test
    void shouldThrowWhenExerciseValueIsGreaterThanTen(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.BREATH_SPEED);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(11.0,exerciseId,"breath");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createExerciseExecution(sessionId,request));
        assertEquals("The value needs to be in range 1-10.",exception.getMessage());
    }
    @Test
    void shouldThrowWhenExerciseValueIsLessThanOne(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.BREATH_SPEED);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(0.0,exerciseId,"breath");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createExerciseExecution(sessionId,request));
        assertEquals("The value needs to be in range 1-10.",exception.getMessage());
    }
    @Test
    void shouldThrowWhenSessionNotFound(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.SCALE);
        when(sessionRepository.findById(any())).thenReturn(Optional.empty());
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(50.0,exerciseId,"idk what i will say here");
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.createExerciseExecution(sessionId,request));
        assertEquals("Session not found",exception.getMessage());
    }
    @Test
    void shouldThrowWhenSessionIsClosed(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.SCALE);
        when(sessionRepository.findById(any())).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.CLOSED);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(10.0,exerciseId,"scale");
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.createExerciseExecution(sessionId,request));
        assertEquals("Session has been closed",exception.getMessage());

    }
    @Test
    void shouldThrowWhenExerciseAlreadyExistsInSession(){
        when(exerciseRepository.findById(any())).thenReturn(Optional.of(exerciseMock));
        when(exerciseMock.getType()).thenReturn(ExerciseType.SCALE);
        when(sessionRepository.findById(any())).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getStatus()).thenReturn(SessionStatus.OPEN);
        when(repository.existsByExercise_IdAndSession_Id(any(), any())).thenReturn(true);
        ExerciseExecutionRequest request = new ExerciseExecutionRequest(10.0,exerciseId,"scale");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.createExerciseExecution(sessionId,request));
        assertEquals("This exercise has already been registered in this session",exception.getMessage());
    }
    @Test
    void shouldGetAllExerciseExecutions(){
        ExerciseExecution executionMock = mock(ExerciseExecution.class);
        when(repository.findAll()).thenReturn(List.of(executionMock));
        when(executionMock.getId()).thenReturn(20L);
        when(executionMock.getValue()).thenReturn(50.0);
        when(executionMock.getExercise()).thenReturn(exerciseMock);
        when(executionMock.getSession()).thenReturn(sessionMock);
        when(executionMock.getNotes()).thenReturn("I'm a mocked object lol");
        when(exerciseMock.getName()).thenReturn("Mock exercise");
        when(sessionMock.getDate()).thenReturn(LocalDate.now());
        ExerciseExecutionResponse response = new ExerciseExecutionResponse(
                20L,50.0, new ExerciseSummary(exerciseMock.getId(),exerciseMock.getName()),
                new SessionSummary(sessionMock.getId(),sessionMock.getDate()),
                "I'm a mocked object lol"
        );
        assertEquals(List.of(response), service.getAllExecutions());

    }




}
