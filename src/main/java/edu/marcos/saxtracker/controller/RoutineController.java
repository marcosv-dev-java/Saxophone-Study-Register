package edu.marcos.saxtracker.controller;

import edu.marcos.saxtracker.service.RoutineService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rotinas")
public class RoutineController {
    private RoutineService service;


    public RoutineController(RoutineService service) {
        this.service = service;
    }

}
