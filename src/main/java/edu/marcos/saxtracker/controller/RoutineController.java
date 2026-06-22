package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.routine.RoutineRequest;
import edu.marcos.saxtracker.dto.routine.RoutineResponse;
import edu.marcos.saxtracker.dto.routine.RoutineUpdateRequest;
import edu.marcos.saxtracker.service.RoutineService;
import edu.marcos.saxtracker.utils.LocationUriBuilder;
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
        return ResponseEntity.created(LocationUriBuilder.buildLocationUri(response.id())).body(response);
    }
    @GetMapping
    public ResponseEntity<List<RoutineResponse>> getAllRoutines(){
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<RoutineResponse> getRoutineById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<RoutineResponse> updateRoutine(@RequestBody RoutineUpdateRequest request, @PathVariable Long id){
        return ResponseEntity.ok(service.updateRoutine(id,request));
    }

    @DeleteMapping("/{id}/desativar")
    public ResponseEntity<RoutineResponse> deactivateRoutine(@PathVariable Long id){
        return ResponseEntity.ok(service.deactivateRoutine(id));
    }
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<RoutineResponse> reactivateRoutine(@PathVariable Long id){
        return ResponseEntity.ok(service.reactivateRoutine(id));
    }

}
