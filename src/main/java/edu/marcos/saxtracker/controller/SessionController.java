package edu.marcos.saxtracker.controller;


import edu.marcos.saxtracker.dto.execution.ExerciseExecutionRequest;
import edu.marcos.saxtracker.dto.execution.ExerciseExecutionResponse;
import edu.marcos.saxtracker.dto.session.SessionRequest;
import edu.marcos.saxtracker.dto.session.SessionResponse;
import edu.marcos.saxtracker.service.ExerciseExecutionService;
import edu.marcos.saxtracker.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sessoes")
public class SessionController {
    private final SessionService service;
    private final ExerciseExecutionService executionService;

    public SessionController(SessionService service, ExerciseExecutionService executionService) {
        this.service = service;
        this.executionService = executionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody @Valid SessionRequest request){
        SessionResponse response = service.createSession(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> findSessionById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }
    @GetMapping("/data/{date}")
    public ResponseEntity<SessionResponse> findByDate(
            @PathVariable LocalDate date
    ) {
        return ResponseEntity.ok(service.findByDate(date));
    }
    @GetMapping
    public ResponseEntity<List<SessionResponse>> findAll(){
        return  ResponseEntity.ok().body(service.findAll());
    }
    @PostMapping("/{id}/execucoes")
    public ResponseEntity<ExerciseExecutionResponse> addExecution(
            @PathVariable Long id,
            @RequestBody @Valid ExerciseExecutionRequest request
    ) {
        ExerciseExecutionResponse response = executionService.createExerciseExecution(id, request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
