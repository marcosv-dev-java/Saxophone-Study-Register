package edu.marcos.saxtracker.dto.error;

import java.util.List;

public record ValidationErrorResponse(int status, List<String> errors) {
}
