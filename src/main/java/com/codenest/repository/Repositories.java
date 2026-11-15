package com.codenest.repository;

import com.codenest.model.Bookmark;
import com.codenest.model.Course;
import com.codenest.model.CourseModule;
import com.codenest.model.LearningStats;
import com.codenest.model.Lesson;
import com.codenest.model.LessonProgress;
import com.codenest.model.PracticeQuestion;
import com.codenest.model.Quiz;
import com.codenest.model.QuizAttempt;
import com.codenest.model.RefreshToken;
import com.codenest.model.User;
import com.codenest.model.UserAchievement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class Repositories {

    private Repositories() {
    }

    public interface UserRepository extends MongoRepository<User, String> {
        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        boolean existsByRole(User.Role role);

        long countByRole(User.Role role);
    }

    public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
        Optional<RefreshToken> findByTokenHash(String tokenHash);

        List<RefreshToken> findByFamilyIdAndRevokedFalse(String familyId);

        List<RefreshToken> findByUserIdAndRevokedFalse(String userId);
    }

    public interface CourseRepository extends MongoRepository<Course, String> {
        Optional<Course> findBySlug(String slug);

        boolean existsBySlug(String slug);

        List<Course> findByPublishedTrueOrderByOrderIndexAsc();

        List<Course> findAllByOrderByOrderIndexAsc();
    }

    public interface CourseModuleRepository extends MongoRepository<CourseModule, String> {
        List<CourseModule> findByCourseIdOrderByOrderIndexAsc(String courseId);

        Optional<CourseModule> findByCourseIdAndSlug(String courseId, String slug);

        boolean existsByCourseIdAndSlug(String courseId, String slug);

        long countByCourseId(String courseId);

        long deleteByCourseId(String courseId);
    }

    public interface LessonRepository extends MongoRepository<Lesson, String> {
        List<Lesson> findByCourseId(String courseId);

        List<Lesson> findByCourseIdAndPublishedTrue(String courseId);

        List<Lesson> findByModuleId(String moduleId);

        Optional<Lesson> findByCourseIdAndSlug(String courseId, String slug);

        boolean existsByCourseIdAndSlug(String courseId, String slug);

        long countByCourseIdAndPublishedTrue(String courseId);

        long deleteByCourseId(String courseId);

        long deleteByModuleId(String moduleId);
    }

    public interface LessonProgressRepository extends MongoRepository<LessonProgress, String> {
        Optional<LessonProgress> findByUserIdAndLessonId(String userId, String lessonId);

        List<LessonProgress> findByUserId(String userId);

        List<LessonProgress> findByUserIdAndCourseId(String userId, String courseId);

        Optional<LessonProgress> findFirstByUserIdOrderByLastVisitedAtDesc(String userId);

        List<LessonProgress> findTop10ByUserIdAndCompletedTrueOrderByCompletedAtDesc(String userId);

        List<LessonProgress> findByUserIdAndCompletedTrueAndCompletedAtGreaterThanEqual(String userId, Instant since);

        long countByUserIdAndCompletedTrue(String userId);

        long countByCompletedTrue();
    }

    public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
        List<Bookmark> findByUserIdOrderByCreatedAtDesc(String userId);

        Optional<Bookmark> findByUserIdAndLessonId(String userId, String lessonId);

        boolean existsByUserIdAndLessonId(String userId, String lessonId);

        long deleteByUserIdAndLessonId(String userId, String lessonId);
    }

    public interface QuizRepository extends MongoRepository<Quiz, String> {
        List<Quiz> findByPublishedTrue();

        List<Quiz> findByCourseIdAndPublishedTrue(String courseId);

        List<Quiz> findByModuleIdAndPublishedTrue(String moduleId);

        List<Quiz> findByLessonIdAndPublishedTrue(String lessonId);

        long deleteByCourseId(String courseId);

        long deleteByModuleId(String moduleId);

        long deleteByLessonId(String lessonId);
    }

    public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
        List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(String userId);

        List<QuizAttempt> findTop10ByUserIdOrderByAttemptedAtDesc(String userId);

        List<QuizAttempt> findByUserIdAndAttemptedAtGreaterThanEqual(String userId, Instant since);

        Optional<QuizAttempt> findByUserIdAndClientAttemptId(String userId, String clientAttemptId);

        Optional<QuizAttempt> findByIdAndUserId(String id, String userId);

        long countByUserId(String userId);
    }

    public interface PracticeQuestionRepository extends MongoRepository<PracticeQuestion, String> {
        List<PracticeQuestion> findByPublishedTrue();

        List<PracticeQuestion> findByCourseIdAndPublishedTrue(String courseId);

        List<PracticeQuestion> findByModuleIdAndPublishedTrue(String moduleId);

        List<PracticeQuestion> findByLessonIdAndPublishedTrue(String lessonId);

        long deleteByCourseId(String courseId);

        long deleteByModuleId(String moduleId);

        long deleteByLessonId(String lessonId);
    }

    public interface UserAchievementRepository extends MongoRepository<UserAchievement, String> {
        List<UserAchievement> findByUserIdOrderByAwardedAtDesc(String userId);

        List<UserAchievement> findTop10ByUserIdOrderByAwardedAtDesc(String userId);

        boolean existsByUserIdAndAchievementId(String userId, String achievementId);

        long countByUserId(String userId);
    }

    public interface LearningStatsRepository extends MongoRepository<LearningStats, String> {
        Optional<LearningStats> findByUserId(String userId);
    }
}