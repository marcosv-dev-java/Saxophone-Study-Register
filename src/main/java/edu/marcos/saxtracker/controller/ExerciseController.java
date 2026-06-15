package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.dto.ExerciseRequest;
import edu.marcos.saxtracker.dto.ExerciseResponse;
import edu.marcos.saxtracker.dto.ExerciseUpdateRequest;
import edu.marcos.saxtracker.model.ExerciseType;
import edu.marcos.saxtracker.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/exercicios")
public class ExerciseController {
    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> add(@RequestBody @Valid ExerciseRequest request){
        ExerciseResponse response = service.save(request);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getAll(
            @RequestParam (required = false) ExerciseType type
    ) {
        if (type != null) {
            return ResponseEntity.ok(service.filterByType(type));
        }

        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(@PathVariable Long id, @Valid @RequestBody ExerciseUpdateRequest request){
        ExerciseResponse response = service.update(id,request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<ExerciseResponse> reactivateExercise(@PathVariable Long id){
        return ResponseEntity.ok(service.reactivate(id));
    }

}
