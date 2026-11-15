package com.codenest.controller;

import com.codenest.dto.Dtos.AdminStats;
import com.codenest.dto.Dtos.CourseRequest;
import com.codenest.dto.Dtos.LessonRequest;
import com.codenest.dto.Dtos.ModuleRequest;
import com.codenest.dto.Dtos.PracticeRequest;
import com.codenest.dto.Dtos.QuizRequest;
import com.codenest.model.Course;
import com.codenest.model.CourseModule;
import com.codenest.model.Lesson;
import com.codenest.model.PracticeQuestion;
import com.codenest.model.Quiz;
import com.codenest.service.ContentService;
import com.codenest.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ContentService content;
    private final QuizService quizzes;

    public AdminController(ContentService content, QuizService quizzes) {
        this.content = content;
        this.quizzes = quizzes;
    }

    // ============================ statistics ============================

    @GetMapping("/stats")
    public AdminStats stats() {
        return content.adminStats();
    }

    // ============================ courses ============================

    @GetMapping("/courses")
    public List<Course> courses() {
        return content.adminCourses();
    }

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@Valid @RequestBody CourseRequest request) {
        return content.createCourse(request);
    }

    @PutMapping("/courses/{id}")
    public Course updateCourse(@PathVariable String id, @Valid @RequestBody CourseRequest request) {
        return content.updateCourse(id, request);
    }

    @DeleteMapping("/courses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable String id) {
        content.deleteCourse(id);
    }

    // ============================ modules ============================

    @GetMapping("/courses/{courseId}/modules")
    public List<CourseModule> modules(@PathVariable String courseId) {
        return content.adminModules(courseId);
    }

    @PostMapping("/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseModule createModule(@Valid @RequestBody ModuleRequest request) {
        return content.createModule(request);
    }

    @PutMapping("/modules/{id}")
    public CourseModule updateModule(@PathVariable String id, @Valid @RequestBody ModuleRequest request) {
        return content.updateModule(id, request);
    }

    @DeleteMapping("/modules/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteModule(@PathVariable String id) {
        content.deleteModule(id);
    }

    // ============================ lessons ============================

    /** Includes unpublished lessons. */
    @GetMapping("/courses/{courseId}/lessons")
    public List<Lesson> lessons(@PathVariable String courseId) {
        return content.adminLessons(courseId);
    }

    @GetMapping("/lessons/{id}")
    public Lesson lesson(@PathVariable String id) {
        return content.adminGetLesson(id);
    }

    @PostMapping("/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public Lesson createLesson(@Valid @RequestBody LessonRequest request) {
        return content.createLesson(request);
    }

    @PutMapping("/lessons/{id}")
    public Lesson updateLesson(@PathVariable String id, @Valid @RequestBody LessonRequest request) {
        return content.updateLesson(id, request);
    }

    @DeleteMapping("/lessons/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLesson(@PathVariable String id) {
        content.deleteLesson(id);
    }

    // ============================ quizzes ============================

    @GetMapping("/quizzes")
    public List<Quiz> quizzes() {
        return quizzes.adminQuizzes();
    }

    @GetMapping("/quizzes/{id}")
    public Quiz quiz(@PathVariable String id) {
        return quizzes.adminGetQuiz(id);
    }

    @PostMapping("/quizzes")
    @ResponseStatus(HttpStatus.CREATED)
    public Quiz createQuiz(@Valid @RequestBody QuizRequest request) {
        return quizzes.createQuiz(request);
    }

    @PutMapping("/quizzes/{id}")
    public Quiz updateQuiz(@PathVariable String id, @Valid @RequestBody QuizRequest request) {
        return quizzes.updateQuiz(id, request);
    }

    @DeleteMapping("/quizzes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable String id) {
        quizzes.deleteQuiz(id);
    }

    @GetMapping("/practice")
    public List<PracticeQuestion> practice() {
        return quizzes.adminPractice();
    }

    @PostMapping("/practice")
    @ResponseStatus(HttpStatus.CREATED)
    public PracticeQuestion createPractice(@Valid @RequestBody PracticeRequest request) {
        return quizzes.createPractice(request);
    }

    @PutMapping("/practice/{id}")
    public PracticeQuestion updatePractice(@PathVariable String id, @Valid @RequestBody PracticeRequest request) {
        return quizzes.updatePractice(id, request);
    }

    @DeleteMapping("/practice/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePractice(@PathVariable String id) {
        quizzes.deletePractice(id);
    }
}