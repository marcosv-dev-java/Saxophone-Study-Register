package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.routine.RoutineRequest;
import edu.marcos.saxtracker.dto.routine.RoutineResponse;
import edu.marcos.saxtracker.service.RoutineService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    @GetMapping
    public ResponseEntity<List<RoutineResponse>> getAllRoutines(){
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<RoutineResponse> getRoutineById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

}
