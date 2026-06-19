package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.routine.RoutineRequest;
import edu.marcos.saxtracker.dto.routine.RoutineResponse;
import edu.marcos.saxtracker.service.RoutineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/rotinas")
public class RoutineController {
    private RoutineService service;


    public RoutineController(RoutineService service) {
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<RoutineResponse> addRoutine(@RequestBody @Valid RoutineRequest request){
        RoutineResponse response = service.createRoutine(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
