package com.codecrafthub.controller;

import com.codecrafthub.model.Course;
import com.codecrafthub.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for creating and managing courses.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * POST /api/courses
     *
     * Creates a new course.
     *
     * @Valid causes Spring to check required fields before the service runs.
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody Course course) {

        Course createdCourse = courseService.createCourse(course);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCourse);
    }

    /**
     * GET /api/courses
     *
     * Returns all courses.
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * GET /api/courses/{id}
     *
     * Returns one course by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    /**
     * PUT /api/courses/{id}
     *
     * Replaces the editable data for an existing course.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody Course course) {

        return ResponseEntity.ok(
                courseService.updateCourse(id, course));
    }

    /**
     * DELETE /api/courses/{id}
     *
     * Deletes an existing course.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/courses/stats
     *
     * Returns statistics about the courses.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCourseStats() {
        List<Course> courses = courseService.getAllCourses();

        Map<String, Long> coursesByStatus = new LinkedHashMap<>();
        coursesByStatus.put("Not Started", 0L);
        coursesByStatus.put("In Progress", 0L);
        coursesByStatus.put("Completed", 0L);

        courses.forEach(course -> {
            String status = String.valueOf(course.getStatus());
            if (coursesByStatus.containsKey(status)) {
                coursesByStatus.put(status, coursesByStatus.get(status) + 1);
            }
        });

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("totalCourses", courses.size());
        statistics.put("coursesByStatus", coursesByStatus);

        return ResponseEntity.ok(statistics);
    }
}