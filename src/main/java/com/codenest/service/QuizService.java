package com.codenest.service;

import com.codenest.dto.Dtos.*;
import com.codenest.exception.ApiException;
import com.codenest.model.CourseModule;
import com.codenest.model.Lesson;
import com.codenest.model.PracticeQuestion;
import com.codenest.model.Quiz;
import com.codenest.model.QuizAttempt;
import com.codenest.repository.Repositories.CourseModuleRepository;
import com.codenest.repository.Repositories.PracticeQuestionRepository;
import com.codenest.repository.Repositories.QuizAttemptRepository;
import com.codenest.repository.Repositories.QuizRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuizService {

    private static final int POINTS_PER_CORRECT_ANSWER = 2;
    private static final int PASS_BONUS = 10;

    private final QuizRepository quizzes;
    private final QuizAttemptRepository attempts;
    private final PracticeQuestionRepository practice;
    private final CourseModuleRepository modules;
    private final ContentService content;
    private final ProgressService progressService;

    public QuizService(QuizRepository quizzes, QuizAttemptRepository attempts,
                       PracticeQuestionRepository practice, CourseModuleRepository modules,
                       ContentService content, ProgressService progressService) {
        this.quizzes = quizzes;
        this.attempts = attempts;
        this.practice = practice;
        this.modules = modules;
        this.content = content;
        this.progressService = progressService;
    }

    // ====================================================================================
    //  STUDENT: QUIZZES
    // ====================================================================================

    /** Filters are optional; the most specific one given (lesson, then module, then course) is used. */
    public List<QuizSummary> listQuizzes(String courseId, String moduleId, String lessonId) {
        List<Quiz> found;
        if (notBlank(lessonId)) {
            found = quizzes.findByLessonIdAndPublishedTrue(lessonId);
        } else if (notBlank(moduleId)) {
            found = quizzes.findByModuleIdAndPublishedTrue(moduleId);
        } else if (notBlank(courseId)) {
            found = quizzes.findByCourseIdAndPublishedTrue(courseId);
        } else {
            found = quizzes.findByPublishedTrue();
        }
        return found.stream()
                .sorted(Comparator.comparing((Quiz q) -> q.title, String.CASE_INSENSITIVE_ORDER))
                .map(QuizService::toSummary)
                .toList();
    }

    /** The answer key and explanations are deliberately not included. */
    public QuizView getQuiz(String id) {
        Quiz quiz = requirePublishedQuiz(id);
        List<QuestionView> questions = quiz.questions.stream()
                .map(q -> new QuestionView(q.id, q.text, q.options))
                .toList();
        return new QuizView(quiz.id, quiz.title, quiz.description, quiz.passingPercent, questions);
    }

    /** Scores the quiz on the server and saves the attempt. Sending the same clientAttemptId again is harmless. */
    public QuizResultView submit(String userId, String quizId, QuizSubmitRequest request) {
        QuizAttempt existing = attempts.findByUserIdAndClientAttemptId(userId, request.clientAttemptId()).orElse(null);
        if (existing != null) {
            return duplicateResult(existing, quizId);
        }

        Quiz quiz = requirePublishedQuiz(quizId);

        Map<String, Integer> chosen = new HashMap<>();
        for (AnswerChoice choice : request.answers()) {
            chosen.putIfAbsent(choice.questionId(), choice.selectedIndex());
        }

        List<QuizAttempt.AnswerResult> results = new ArrayList<>();
        int correct = 0;
        for (Quiz.Question q : quiz.questions) {
            int selected = chosen.getOrDefault(q.id, -1);
            if (selected < -1 || selected >= q.options.size()) {
                throw ApiException.badRequest("One of the answers is not a valid option.");
            }
            boolean isCorrect = selected == q.correctIndex;
            if (isCorrect) {
                correct++;
            }
            QuizAttempt.AnswerResult r = new QuizAttempt.AnswerResult();
            r.questionId = q.id;
            r.questionText = q.text;
            r.selectedIndex = selected;
            r.correctIndex = q.correctIndex;
            r.correct = isCorrect;
            r.explanation = q.explanation;
            results.add(r);
        }

        int total = quiz.questions.size();
        int score = total == 0 ? 0 : correct * 100 / total;
        boolean passed = score >= quiz.passingPercent;

        // Points are given for the first attempt at each quiz only, so retakes cannot be used to farm points.
        boolean firstAttempt = attempts.findByUserIdOrderByAttemptedAtDesc(userId).stream()
                .noneMatch(a -> quizId.equals(a.quizId));
        int points = firstAttempt ? correct * POINTS_PER_CORRECT_ANSWER + (passed ? PASS_BONUS : 0) : 0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.userId = userId;
        attempt.quizId = quiz.id;
        attempt.quizTitle = quiz.title;
        attempt.clientAttemptId = request.clientAttemptId();
        attempt.attemptedAt = Instant.now();
        attempt.totalQuestions = total;
        attempt.correctAnswers = correct;
        attempt.scorePercent = score;
        attempt.passed = passed;
        attempt.pointsAwarded = points;
        attempt.answers = results;

        try {
            attempt = attempts.save(attempt);
        } catch (DuplicateKeyException e) {
            // The same submission arrived twice at once; the other request saved it first.
            QuizAttempt saved = attempts.findByUserIdAndClientAttemptId(userId, request.clientAttemptId())
                    .orElseThrow(() -> ApiException.conflict("Could not save the quiz result. Please try again."));
            return duplicateResult(saved, quizId);
        }

        List<AchievementView> fresh = progressService.onQuizCompleted(userId, points);
        return toResult(attempt, false, fresh);
    }

    public List<AttemptSummary> history(String userId) {
        return attempts.findByUserIdOrderByAttemptedAtDesc(userId).stream()
                .map(QuizService::toAttemptSummary)
                .toList();
    }

    /** Looked up by attempt id AND the signed-in user, so another student's attempt looks like "not found". */
    public QuizResultView attemptDetail(String userId, String attemptId) {
        QuizAttempt attempt = attempts.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> ApiException.notFound("Quiz attempt not found."));
        return toResult(attempt, false, List.of());
    }

    private QuizResultView duplicateResult(QuizAttempt existing, String quizId) {
        if (!existing.quizId.equals(quizId)) {
            throw ApiException.conflict("This submission id was already used for a different quiz.");
        }
        return toResult(existing, true, List.of());
    }

    // ====================================================================================
    //  STUDENT: PRACTICE QUESTIONS
    // ====================================================================================

    public List<PracticeView> listPractice(String courseId, String moduleId, String lessonId) {
        List<PracticeQuestion> found;
        if (notBlank(lessonId)) {
            found = practice.findByLessonIdAndPublishedTrue(lessonId);
        } else if (notBlank(moduleId)) {
            found = practice.findByModuleIdAndPublishedTrue(moduleId);
        } else if (notBlank(courseId)) {
            found = practice.findByCourseIdAndPublishedTrue(courseId);
        } else {
            found = practice.findByPublishedTrue();
        }
        return found.stream()
                .sorted(Comparator.comparing((PracticeQuestion p) -> p.title, String.CASE_INSENSITIVE_ORDER))
                .map(p -> new PracticeView(p.id, p.courseId, p.moduleId, p.lessonId, p.title,
                        p.description, p.difficulty, p.starterCode, p.hint))
                .toList();
    }

    /** The reference solution is sent only when the student explicitly asks for it. */
    public PracticeSolution practiceSolution(String id) {
        PracticeQuestion p = practice.findById(id)
                .filter(x -> x.published)
                .orElseThrow(() -> ApiException.notFound("Practice question not found."));
        return new PracticeSolution(p.id, p.referenceSolution, p.expectedOutput);
    }

    // ====================================================================================
    //  ADMIN: QUIZZES
    // ====================================================================================

    public List<Quiz> adminQuizzes() {
        return quizzes.findAll().stream()
                .sorted(Comparator.comparing((Quiz q) -> q.title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Quiz adminGetQuiz(String id) {
        return requireQuiz(id);
    }

    public Quiz createQuiz(QuizRequest request) {
        Quiz quiz = new Quiz();
        applyQuiz(quiz, request);
        return quizzes.save(quiz);
    }

    public Quiz updateQuiz(String id, QuizRequest request) {
        Quiz quiz = requireQuiz(id);
        applyQuiz(quiz, request);
        return quizzes.save(quiz);
    }

    /** Past attempts are kept: each one stores its own snapshot of the questions and answers. */
    public void deleteQuiz(String id) {
        requireQuiz(id);
        quizzes.deleteById(id);
    }

    private void applyQuiz(Quiz quiz, QuizRequest r) {
        String[] scope = resolveScope(r.courseId(), r.moduleId(), r.lessonId());

        List<Quiz.Question> built = new ArrayList<>();
        for (QuestionRequest qr : r.questions()) {
            if (qr.correctIndex() >= qr.options().size()) {
                throw ApiException.badRequest("A correct-answer position points outside the list of options.");
            }
            Quiz.Question q = new Quiz.Question();
            q.id = UUID.randomUUID().toString();
            q.text = qr.text().trim();
            q.options = new ArrayList<>(qr.options().stream().map(String::trim).toList());
            q.correctIndex = qr.correctIndex();
            q.explanation = ContentService.trimOrNull(qr.explanation());
            built.add(q);
        }

        quiz.courseId = r.courseId();
        quiz.moduleId = scope[0];
        quiz.lessonId = scope[1];
        quiz.title = r.title().trim();
        quiz.description = ContentService.trimOrNull(r.description());
        quiz.passingPercent = r.passingPercent() != null ? r.passingPercent() : 60;
        quiz.questions = built;
        if (r.published() != null) {
            quiz.published = r.published();
        }
        quiz.updatedAt = Instant.now();
    }

    // ====================================================================================
    //  ADMIN: PRACTICE QUESTIONS
    // ====================================================================================

    public List<PracticeQuestion> adminPractice() {
        return practice.findAll().stream()
                .sorted(Comparator.comparing((PracticeQuestion p) -> p.title, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public PracticeQuestion createPractice(PracticeRequest request) {
        PracticeQuestion p = new PracticeQuestion();
        applyPractice(p, request);
        return practice.save(p);
    }

    public PracticeQuestion updatePractice(String id, PracticeRequest request) {
        PracticeQuestion p = practice.findById(id)
                .orElseThrow(() -> ApiException.notFound("Practice question not found."));
        applyPractice(p, request);
        return practice.save(p);
    }

    public void deletePractice(String id) {
        practice.findById(id).orElseThrow(() -> ApiException.notFound("Practice question not found."));
        practice.deleteById(id);
    }

    private void applyPractice(PracticeQuestion p, PracticeRequest r) {
        String[] scope = resolveScope(r.courseId(), r.moduleId(), r.lessonId());
        p.courseId = r.courseId();
        p.moduleId = scope[0];
        p.lessonId = scope[1];
        p.title = r.title().trim();
        p.description = r.description().trim();
        p.difficulty = r.difficulty() != null ? r.difficulty() : "EASY";
        p.starterCode = ContentService.trimOrNull(r.starterCode());
        p.hint = ContentService.trimOrNull(r.hint());
        p.referenceSolution = r.referenceSolution();
        p.expectedOutput = ContentService.trimOrNull(r.expectedOutput());
        if (r.published() != null) {
            p.published = r.published();
        }
        p.updatedAt = Instant.now();
    }

    // ====================================================================================
    //  HELPERS
    // ====================================================================================

    /**
     * Checks that the course, module and lesson exist and belong together. Returns {moduleId, lessonId};
     * when only a lesson is given, its module is filled in automatically.
     */
    private String[] resolveScope(String courseId, String moduleId, String lessonId) {
        content.requireCourse(courseId);
        String module = ContentService.trimOrNull(moduleId);
        String lesson = ContentService.trimOrNull(lessonId);

        if (lesson != null) {
            Lesson l = content.requireLesson(lesson);
            if (!l.courseId.equals(courseId)) {
                throw ApiException.badRequest("The selected lesson belongs to a different course.");
            }
            if (module != null && !module.equals(l.moduleId)) {
                throw ApiException.badRequest("The selected lesson is not in the selected module.");
            }
            module = l.moduleId;
        }
        if (module != null) {
            CourseModule m = modules.findById(module)
                    .orElseThrow(() -> ApiException.badRequest("The selected module does not exist."));
            if (!m.courseId.equals(courseId)) {
                throw ApiException.badRequest("The selected module belongs to a different course.");
            }
        }
        return new String[]{module, lesson};
    }

    private Quiz requireQuiz(String id) {
        return quizzes.findById(id).orElseThrow(() -> ApiException.notFound("Quiz not found."));
    }

    private Quiz requirePublishedQuiz(String id) {
        Quiz quiz = requireQuiz(id);
        if (!quiz.published) {
            throw ApiException.notFound("Quiz not found.");
        }
        return quiz;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static QuizSummary toSummary(Quiz q) {
        return new QuizSummary(q.id, q.title, q.description, q.courseId, q.moduleId, q.lessonId,
                q.questions.size(), q.passingPercent);
    }

    static AttemptSummary toAttemptSummary(QuizAttempt a) {
        return new AttemptSummary(a.id, a.quizId, a.quizTitle, a.attemptedAt, a.totalQuestions,
                a.correctAnswers, a.scorePercent, a.passed, a.pointsAwarded);
    }

    private static QuizResultView toResult(QuizAttempt a, boolean duplicate, List<AchievementView> fresh) {
        List<AnswerDetail> details = a.answers.stream()
                .map(r -> new AnswerDetail(r.questionId, r.questionText, r.selectedIndex,
                        r.correctIndex, r.correct, r.explanation))
                .toList();
        return new QuizResultView(a.id, a.quizId, a.quizTitle, a.attemptedAt, a.totalQuestions,
                a.correctAnswers, a.scorePercent, a.passed, a.pointsAwarded, duplicate, details, fresh);
    }
}