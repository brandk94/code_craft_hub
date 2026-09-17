package com.codecrafthub.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts Java exceptions into useful JSON API error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing or blank required fields.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message);
    }

    /**
     * Handles malformed JSON, invalid status values, and invalid dates.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(
            HttpMessageNotReadableException exception) {

        Throwable cause = exception.getMostSpecificCause();
        String message = "Request body contains invalid JSON";

        if (cause instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException
                    .getPath()
                    .stream()
                    .findFirst()
                    .map(reference -> reference.getFieldName())
                    .orElse("field");

            if (invalidFormatException.getTargetType() == LocalDate.class) {
                message = fieldName
                        + " must use the format YYYY-MM-DD";
            } else {
                message = "Invalid value for field: " + fieldName;
            }
        } else if (cause.getMessage() != null
                && cause.getMessage().contains("Status must be exactly")) {
            message = cause.getMessage();
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles requests for courses that do not exist.
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCourseNotFound(
            CourseNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    /**
     * Handles file creation, reading, and writing failures.
     */
    @ExceptionHandler(DataFileException.class)
    public ResponseEntity<Map<String, Object>> handleDataFileError(
            DataFileException exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage());
    }

    /**
     * Handles malformed path variables such as:
     * GET /api/courses/not-a-number
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatError(
            NumberFormatException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Course id must be a number");
    }

    /**
     * Handles unknown API routes.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEndpointNotFound(
            NoResourceFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "API endpoint not found");
    }

    /**
     * Final fallback for unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(
            Exception exception) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred");
    }

    /**
     * Creates a consistent error response body.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}