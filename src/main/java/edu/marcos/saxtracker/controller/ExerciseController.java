package edu.marcos.saxtracker.controller;
import edu.marcos.saxtracker.dto.exercise.ExerciseRequest;
import edu.marcos.saxtracker.dto.exercise.ExerciseResponse;
import edu.marcos.saxtracker.dto.exercise.ExerciseUpdateRequest;
import edu.marcos.saxtracker.model.ExerciseType;
import edu.marcos.saxtracker.service.ExerciseService;
import edu.marcos.saxtracker.utils.LocationUriBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        return ResponseEntity.created(LocationUriBuilder.buildLocationUri(response.id())).body(response);
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
    public ResponseEntity<ExerciseResponse> update(@PathVariable Long id,@RequestBody ExerciseUpdateRequest request){
        ExerciseResponse response = service.update(id,request);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ExerciseResponse> reactivateExercise(@PathVariable Long id){
        return ResponseEntity.ok(service.reactivate(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> hardDeleteById(@PathVariable Long id){
        service.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/desativar")
    public ResponseEntity<ExerciseResponse> softDeleteById(@PathVariable Long id){
        ExerciseResponse response = service.softDelete(id);
        return ResponseEntity.ok().body(response);
    }

}
