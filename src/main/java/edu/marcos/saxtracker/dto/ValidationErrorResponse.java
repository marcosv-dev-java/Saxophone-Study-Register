package edu.marcos.saxtracker.dto;

import java.util.List;

public record ValidationErrorResponse(int status, List<String> errors) {
}
