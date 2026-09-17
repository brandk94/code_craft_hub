package com.codecrafthub.exception;

/**
 * Thrown when the courses.json file cannot be created, read, or written.
 */
public class DataFileException extends RuntimeException {

    public DataFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFileException(String message) {
        super(message);
    }
}