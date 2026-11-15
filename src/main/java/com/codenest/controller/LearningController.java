package com.codenest.controller;

import com.codenest.dto.Dtos.AchievementView;
import com.codenest.dto.Dtos.ActivityItem;
import com.codenest.dto.Dtos.AttemptSummary;
import com.codenest.dto.Dtos.BookmarkView;
import com.codenest.dto.Dtos.CompletionResult;
import com.codenest.dto.Dtos.DashboardView;
import com.codenest.dto.Dtos.DayCount;
import com.codenest.dto.Dtos.OverallProgress;
import com.codenest.dto.Dtos.PracticeSolution;
import com.codenest.dto.Dtos.PracticeView;
import com.codenest.dto.Dtos.ProgressUpdateRequest;
import com.codenest.dto.Dtos.QuizResultView;
import com.codenest.dto.Dtos.QuizSubmitRequest;
import com.codenest.dto.Dtos.QuizSummary;
import com.codenest.dto.Dtos.QuizView;
import com.codenest.dto.Dtos.ResumeView;
import com.codenest.dto.Dtos.StreakView;
import com.codenest.service.ProgressService;
import com.codenest.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LearningController {

    private final ProgressService progress;
    private final QuizService quizzes;

    public LearningController(ProgressService progress, QuizService quizzes) {
        this.progress = progress;
        this.quizzes = quizzes;
    }

    // ============================ lessons: save and complete ============================

    /** Records the lesson as last active and stores any reading position, video position or mode in the body. */
    @PutMapping("/lessons/{lessonId}/progress")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveProgress(Authentication authentication, @PathVariable String lessonId,
                             @Valid @RequestBody(required = false) ProgressUpdateRequest request) {
        progress.saveProgress(authentication.getName(), lessonId, request);
    }

    @PostMapping("/lessons/{lessonId}/complete")
    public CompletionResult complete(Authentication authentication, @PathVariable String lessonId) {
        return progress.completeLesson(authentication.getName(), lessonId);
    }

    // ============================ dashboard and progress views ============================

    @GetMapping("/dashboard")
    public DashboardView dashboard(Authentication authentication) {
        return progress.dashboard(authentication.getName());
    }

    @GetMapping("/progress")
    public OverallProgress overall(Authentication authentication) {
        return progress.overall(authentication.getName());
    }

    /** The "Continue Learning" target. */
    @GetMapping("/progress/resume")
    public ResumeView resume(Authentication authentication) {
        return progress.resume(authentication.getName());
    }

    @GetMapping("/progress/activity")
    public List<ActivityItem> activity(Authentication authentication) {
        return progress.recentActivity(authentication.getName());
    }

    @GetMapping("/progress/weekly")
    public List<DayCount> weekly(Authentication authentication) {
        return progress.weekly(authentication.getName());
    }

    @GetMapping("/progress/streak")
    public StreakView streak(Authentication authentication) {
        return progress.streak(authentication.getName());
    }

    @GetMapping("/progress/achievements")
    public List<AchievementView> achievements(Authentication authentication) {
        return progress.achievements(authentication.getName());
    }

    // ============================ bookmarks ============================

    @GetMapping("/bookmarks")
    public List<BookmarkView> bookmarks(Authentication authentication) {
        return progress.bookmarks(authentication.getName());
    }

    /** PUT is idempotent: adding the same bookmark twice is harmless. */
    @PutMapping("/bookmarks/{lessonId}")
    public BookmarkView addBookmark(Authentication authentication, @PathVariable String lessonId) {
        return progress.addBookmark(authentication.getName(), lessonId);
    }

    @DeleteMapping("/bookmarks/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBookmark(Authentication authentication, @PathVariable String lessonId) {
        progress.removeBookmark(authentication.getName(), lessonId);
    }

    // ============================ quizzes ============================

    @GetMapping("/quizzes")
    public List<QuizSummary> quizzes(@RequestParam(required = false) String courseId,
                                     @RequestParam(required = false) String moduleId,
                                     @RequestParam(required = false) String lessonId) {
        return quizzes.listQuizzes(courseId, moduleId, lessonId);
    }

    /** No answer key is included. */
    @GetMapping("/quizzes/{quizId}")
    public QuizView quiz(@PathVariable String quizId) {
        return quizzes.getQuiz(quizId);
    }

    /** Scored on the server. Sending the same clientAttemptId again returns the original result. */
    @PostMapping("/quizzes/{quizId}/attempts")
    public QuizResultView submit(Authentication authentication, @PathVariable String quizId,
                                 @Valid @RequestBody QuizSubmitRequest request) {
        return quizzes.submit(authentication.getName(), quizId, request);
    }

    @GetMapping("/quiz-attempts")
    public List<AttemptSummary> history(Authentication authentication) {
        return quizzes.history(authentication.getName());
    }

    @GetMapping("/quiz-attempts/{attemptId}")
    public QuizResultView attempt(Authentication authentication, @PathVariable String attemptId) {
        return quizzes.attemptDetail(authentication.getName(), attemptId);
    }

    // ============================ practice questions ============================

    @GetMapping("/practice")
    public List<PracticeView> practice(@RequestParam(required = false) String courseId,
                                       @RequestParam(required = false) String moduleId,
                                       @RequestParam(required = false) String lessonId) {
        return quizzes.listPractice(courseId, moduleId, lessonId);
    }

    /** Fetched only when the student chooses to reveal the reference solution. */
    @GetMapping("/practice/{id}/solution")
    public PracticeSolution solution(@PathVariable String id) {
        return quizzes.practiceSolution(id);
    }
}