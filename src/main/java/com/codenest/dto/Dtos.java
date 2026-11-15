package com.codenest.dto;

import com.codenest.model.Lesson;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class Dtos {

    private Dtos() {
    }

    // ============================ ERRORS ============================

    public record ErrorResponse(Instant timestamp, int status, String code, String message,
                                Map<String, String> fieldErrors) {
    }

    // ============================ AUTH & PROFILE ============================

    public record RegisterRequest(
            @NotBlank @Size(min = 2, max = 60) String name,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 8, max = 72) String password) {
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 72) String password) {
    }

    public record UserView(String id, String name, String email, String role, Instant createdAt) {
    }

    /** Returned by register, login and refresh. The refresh token itself travels only in an HttpOnly cookie. */
    public record AuthResponse(String accessToken, long expiresInSeconds, UserView user) {
    }

    public record ProfileUpdateRequest(
            @NotBlank @Size(min = 2, max = 60) String name) {
    }

    public record PasswordChangeRequest(
            @NotBlank @Size(max = 72) String currentPassword,
            @NotBlank @Size(min = 8, max = 72) String newPassword) {
    }

    // ============================ COURSES & LESSONS (student view) ============================

    public record LessonSummary(String id, String title, int orderIndex, int estimatedMinutes,
                                boolean hasVideo, boolean completed) {
    }

    public record ModuleView(String id, String title, String description, int orderIndex,
                             List<LessonSummary> lessons) {
    }

    public record CourseSummary(String id, String title, String description, String level,
                                int totalModules, int totalLessons) {
    }

    public record CourseDetail(String id, String title, String description, String level,
                               int totalLessons, List<ModuleView> modules) {
    }

    public record LessonDetail(
            String id, String courseId, String courseTitle, String moduleId, String moduleTitle,
            String title, int estimatedMinutes,
            List<String> objectives, String explanation, List<Lesson.CodeExample> examples,
            List<String> importantNotes, List<String> commonMistakes, List<String> relatedTopics,
            String youtubeVideoId, String videoTitle,
            String previousLessonId, String nextLessonId,
            boolean completed, boolean bookmarked,
            double readingPosition, double videoPositionSeconds, String lastMode) {
    }

    // ============================ PROGRESS ============================

    /** Every field is optional; only the fields that are sent are updated. */
    public record ProgressUpdateRequest(
            @DecimalMin("0.0") @DecimalMax("1.0") Double readingPosition,
            @DecimalMin("0.0") @DecimalMax("86400.0") Double videoPositionSeconds,
            @Pattern(regexp = "READING|VIDEO") String mode) {
    }

    public record CourseProgressView(String courseId, String courseTitle, int totalLessons,
                                     int completedLessons, int percent, boolean completed,
                                     String lastLessonId, Instant lastActivityAt) {
    }

    public record OverallProgress(int totalLessons, int completedLessons, int percent,
                                  int totalPoints, int quizzesCompleted,
                                  List<CourseProgressView> courses) {
    }

    /** "started" is false when the student has never opened a lesson; the lesson is then a suggested first lesson. */
    public record ResumeView(boolean started, String courseId, String courseTitle, String moduleId,
                             String lessonId, String lessonTitle, Instant lastVisitedAt) {
    }

    public record StreakView(int currentStreak, int longestStreak, LocalDate lastActivityDate,
                             int totalLearningDays, boolean activeToday) {
    }

    public record AchievementView(String id, String title, String description, int points,
                                  boolean earned, Instant awardedAt) {
    }

    public record CompletionResult(String lessonId, boolean newlyCompleted, int pointsAwarded,
                                   int totalPoints, CourseProgressView course, StreakView streak,
                                   List<AchievementView> newAchievements) {
    }

    public record ActivityItem(String type, String title, String refId, int points, Instant occurredAt) {
    }

    public record DayCount(LocalDate day, int count) {
    }

    public record BookmarkView(String lessonId, String lessonTitle, String courseId, String courseTitle,
                               Instant createdAt) {
    }

    public record DashboardView(String name, ResumeView resume, OverallProgress overall, StreakView streak,
                                List<ActivityItem> recentActivity, List<AttemptSummary> recentQuizzes,
                                List<BookmarkView> bookmarks, List<AchievementView> achievements,
                                List<DayCount> weekly) {
    }

    // ============================ QUIZZES & PRACTICE (student view) ============================

    public record QuizSummary(String id, String title, String description, String courseId, String moduleId,
                              String lessonId, int questionCount, int passingPercent) {
    }

    /** No answer key and no explanation: those are revealed only after submission. */
    public record QuestionView(String id, String text, List<String> options) {
    }

    public record QuizView(String id, String title, String description, int passingPercent,
                           List<QuestionView> questions) {
    }

    /** selectedIndex of -1 means the question was left unanswered. */
    public record AnswerChoice(
            @NotBlank @Size(max = 64) String questionId,
            @Min(-1) @Max(20) int selectedIndex) {
    }

    /** clientAttemptId is a UUID created once per submission; resending it can never create a second attempt. */
    public record QuizSubmitRequest(
            @NotBlank @Size(max = 64) String clientAttemptId,
            @NotEmpty @Size(max = 100) @Valid List<AnswerChoice> answers) {
    }

    public record AnswerDetail(String questionId, String questionText, int selectedIndex, int correctIndex,
                               boolean correct, String explanation) {
    }

    public record QuizResultView(String attemptId, String quizId, String quizTitle, Instant attemptedAt,
                                 int totalQuestions, int correctAnswers, int scorePercent, boolean passed,
                                 int pointsAwarded, boolean duplicate, List<AnswerDetail> answers,
                                 List<AchievementView> newAchievements) {
    }

    public record AttemptSummary(String id, String quizId, String quizTitle, Instant attemptedAt,
                                 int totalQuestions, int correctAnswers, int scorePercent,
                                 boolean passed, int pointsAwarded) {
    }

    /** The reference solution is deliberately excluded; it is fetched separately via PracticeSolution. */
    public record PracticeView(String id, String courseId, String moduleId, String lessonId, String title,
                               String description, String difficulty, String starterCode, String hint) {
    }

    public record PracticeSolution(String id, String referenceSolution, String expectedOutput) {
    }

    // ============================ ADMIN REQUESTS ============================

    public record CourseRequest(
            @NotBlank @Size(max = 120) String title,
            @Size(max = 2000) String description,
            @Size(max = 40) String level,
            @Min(0) int orderIndex,
            Boolean published) {
    }

    public record ModuleRequest(
            @NotBlank String courseId,
            @NotBlank @Size(max = 120) String title,
            @Size(max = 2000) String description,
            @Min(0) int orderIndex) {
    }

    public record CodeExampleRequest(
            @Size(max = 200) String title,
            @NotBlank @Size(max = 20000) String code,
            @Size(max = 5000) String expectedOutput) {
    }

    /** youtubeUrl accepts a full YouTube URL or a bare 11-character video ID; the backend stores only the ID. */
    public record LessonRequest(
            @NotBlank String courseId,
            @NotBlank String moduleId,
            @NotBlank @Size(max = 150) String title,
            @Min(0) int orderIndex,
            @Min(1) @Max(600) Integer estimatedMinutes,
            List<@Size(max = 300) String> objectives,
            @NotBlank @Size(max = 50000) String explanation,
            @Valid List<CodeExampleRequest> examples,
            List<@Size(max = 1000) String> importantNotes,
            List<@Size(max = 1000) String> commonMistakes,
            List<@Size(max = 150) String> relatedTopics,
            @Size(max = 200) String youtubeUrl,
            @Size(max = 200) String videoTitle,
            Boolean published) {
    }

    public record QuestionRequest(
            @NotBlank @Size(max = 1000) String text,
            @NotEmpty @Size(min = 2, max = 6) List<@NotBlank @Size(max = 300) String> options,
            @Min(0) @Max(5) int correctIndex,
            @Size(max = 2000) String explanation) {
    }

    public record QuizRequest(
            @NotBlank String courseId,
            String moduleId,
            String lessonId,
            @NotBlank @Size(max = 150) String title,
            @Size(max = 1000) String description,
            @Min(1) @Max(100) Integer passingPercent,
            @NotEmpty @Size(max = 50) @Valid List<QuestionRequest> questions,
            Boolean published) {
    }

    public record PracticeRequest(
            @NotBlank String courseId,
            String moduleId,
            String lessonId,
            @NotBlank @Size(max = 150) String title,
            @NotBlank @Size(max = 5000) String description,
            @Pattern(regexp = "EASY|MEDIUM|HARD") String difficulty,
            @Size(max = 10000) String starterCode,
            @Size(max = 1000) String hint,
            @NotBlank @Size(max = 20000) String referenceSolution,
            @Size(max = 5000) String expectedOutput,
            Boolean published) {
    }

    public record AdminStats(long users, long students, long admins, long courses, long modules,
                             long lessons, long quizzes, long practiceQuestions, long quizAttempts,
                             long lessonCompletions) {
    }
}