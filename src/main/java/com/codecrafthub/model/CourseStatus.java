package com.codecrafthub.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The only status values allowed by the API.
 *
 * The JSON values intentionally contain spaces and use title case.
 */
public enum CourseStatus {

    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String value;

    CourseStatus(String value) {
        this.value = value;
    }

    /**
     * Controls how the enum is written to JSON responses.
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Controls how JSON status values are converted into enum values.
     *
     * Matching is exact. For example, "completed" is invalid.
     */
    @JsonCreator
    public static CourseStatus fromValue(String value) {
        for (CourseStatus status : CourseStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Status must be exactly one of: Not Started, In Progress, Completed");
    }
}