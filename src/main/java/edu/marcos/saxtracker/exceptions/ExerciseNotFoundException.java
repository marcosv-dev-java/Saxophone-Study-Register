package edu.marcos.saxtracker.exceptions;

public class ExerciseNotFoundException extends RuntimeException {
    public ExerciseNotFoundException(String message) {
        super(message);
    }

    public ExerciseNotFoundException() {
        super("Index not found!");
    }
}
