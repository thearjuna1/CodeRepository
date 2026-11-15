package com.codenest.service;

import com.codenest.dto.Dtos.*;
import com.codenest.exception.ApiException;
import com.codenest.model.Bookmark;
import com.codenest.model.Course;
import com.codenest.model.LearningStats;
import com.codenest.model.Lesson;
import com.codenest.model.LessonProgress;
import com.codenest.model.QuizAttempt;
import com.codenest.model.UserAchievement;
import com.codenest.repository.Repositories.BookmarkRepository;
import com.codenest.repository.Repositories.CourseRepository;
import com.codenest.repository.Repositories.LearningStatsRepository;
import com.codenest.repository.Repositories.LessonProgressRepository;
import com.codenest.repository.Repositories.LessonRepository;
import com.codenest.repository.Repositories.QuizAttemptRepository;
import com.codenest.repository.Repositories.UserAchievementRepository;
import com.codenest.repository.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    public static final int LESSON_POINTS = 10;

    private final LessonProgressRepository progress;
    private final BookmarkRepository bookmarkRepo;
    private final LearningStatsRepository statsRepo;
    private final UserAchievementRepository achievementRepo;
    private final QuizAttemptRepository attempts;
    private final LessonRepository lessons;
    private final CourseRepository courses;
    private final UserRepository users;
    private final ContentService content;
    private final MongoTemplate mongo;
    private final ZoneId zone;

    public ProgressService(LessonProgressRepository progress, BookmarkRepository bookmarkRepo,
                           LearningStatsRepository statsRepo, UserAchievementRepository achievementRepo,
                           QuizAttemptRepository attempts, LessonRepository lessons, CourseRepository courses,
                           UserRepository users, ContentService content, MongoTemplate mongo,
                           @Value("${codenest.learning.timezone}") String timezone) {
        this.progress = progress;
        this.bookmarkRepo = bookmarkRepo;
        this.statsRepo = statsRepo;
        this.achievementRepo = achievementRepo;
        this.attempts = attempts;
        this.lessons = lessons;
        this.courses = courses;
        this.users = users;
        this.content = content;
        this.mongo = mongo;
        this.zone = ZoneId.of(timezone);
    }

    // ====================================================================================
    //  SAVING PROGRESS (idempotent)
    // ====================================================================================

    /**
     * Records that the student opened the lesson (this is what "last active lesson" is built from) and
     * stores any reading position, video position or mode that was sent. Safe to repeat any number of times.
     */
    public void saveProgress(String userId, String lessonId, ProgressUpdateRequest request) {
        Lesson lesson = content.requirePublishedLesson(lessonId);
        touch(userId, lesson, request);
    }

    private void touch(String userId, Lesson lesson, ProgressUpdateRequest r) {
        Instant now = Instant.now();
        Query query = new Query(Criteria.where("userId").is(userId).and("lessonId").is(lesson.id));
        Update update = new Update()
                .setOnInsert("courseId", lesson.courseId)
                .setOnInsert("moduleId", lesson.moduleId)
                .setOnInsert("completed", false)
                .setOnInsert("firstVisitedAt", now)
                .set("lastVisitedAt", now);
        if (r != null) {
            if (r.readingPosition() != null) {
                update.set("readingPosition", r.readingPosition());
            }
            if (r.videoPositionSeconds() != null) {
                update.set("videoPositionSeconds", r.videoPositionSeconds());
            }
            if (r.mode() != null) {
                update.set("lastMode", r.mode());
            }
        }
        try {
            mongo.upsert(query, update, LessonProgress.class);
        } catch (DuplicateKeyException e) {
            // Two requests created the record at the same moment; the record now exists, so update it.
            mongo.upsert(query, update, LessonProgress.class);
        }
    }

    // ====================================================================================
    //  COMPLETING A LESSON
    // ====================================================================================

    public CompletionResult completeLesson(String userId, String lessonId) {
        Lesson lesson = content.requirePublishedLesson(lessonId);
        Course course = content.requirePublishedCourse(lesson.courseId);

        touch(userId, lesson, null); // makes sure the progress record exists

        // Atomic claim: only the request that flips completed from false to true gets a non-null result.
        Instant now = Instant.now();
        Query claim = new Query(Criteria.where("userId").is(userId)
                .and("lessonId").is(lessonId).and("completed").is(false));
        Update done = new Update()
                .set("completed", true)
                .set("completedAt", now)
                .set("lastVisitedAt", now);
        boolean newlyCompleted = mongo.findAndModify(claim, done, LessonProgress.class) != null;

        int points = 0;
        List<AchievementView> fresh = List.of();

        if (newlyCompleted) {
            points = LESSON_POINTS;
            final int lessonPoints = points;
            final long completedCount = progress.countByUserIdAndCompletedTrue(userId);
            final LocalDate today = LocalDate.now(zone);

            LearningStats stats = mutateStats(userId, s -> {
                s.totalPoints += lessonPoints;
                s.lessonsCompleted = (int) completedCount;
                recordLearningDay(s, today);
            });

            boolean courseDone = courseProgress(userId, course).completed();
            fresh = award(userId, eligible(stats, courseDone));
        }

        LearningStats latest = statsRepo.findByUserId(userId).orElseGet(() -> emptyStats(userId));
        return new CompletionResult(lessonId, newlyCompleted, points, latest.totalPoints,
                courseProgress(userId, course), streakView(latest), fresh);
    }

    /** Called by QuizService after a new quiz attempt has been saved. */
    public List<AchievementView> onQuizCompleted(String userId, int pointsAwarded) {
        final long attemptCount = attempts.countByUserId(userId);
        final LocalDate today = LocalDate.now(zone);
        LearningStats stats = mutateStats(userId, s -> {
            s.totalPoints += pointsAwarded;
            s.quizzesCompleted = (int) attemptCount;
            recordLearningDay(s, today);
        });
        return award(userId, eligible(stats, false));
    }

    // ====================================================================================
    //  STREAK, POINTS AND ACHIEVEMENT RULES
    // ====================================================================================

    private static void recordLearningDay(LearningStats s, LocalDate today) {
        LocalDate last = s.lastActivityDate;
        if (last == null) {
            s.currentStreak = 1;
            s.totalLearningDays = 1;
            s.lastActivityDate = today;
        } else if (last.equals(today)) {
            return; // already counted today
        } else if (last.equals(today.minusDays(1))) {
            s.currentStreak += 1;
            s.totalLearningDays += 1;
            s.lastActivityDate = today;
        } else if (last.isBefore(today)) {
            s.currentStreak = 1; // a gap of a day or more restarts the streak
            s.totalLearningDays += 1;
            s.lastActivityDate = today;
        } else {
            return; // the stored date is in the future (clock change); do not touch the streak
        }
        s.longestStreak = Math.max(s.longestStreak, s.currentStreak);
    }

    private static Set<UserAchievement.Type> eligible(LearningStats s, boolean courseCompleted) {
        Set<UserAchievement.Type> set = EnumSet.noneOf(UserAchievement.Type.class);
        if (s.lessonsCompleted >= 1) {
            set.add(UserAchievement.Type.FIRST_LESSON);
        }
        if (s.lessonsCompleted >= 5) {
            set.add(UserAchievement.Type.FIVE_LESSONS);
        }
        if (s.lessonsCompleted >= 10) {
            set.add(UserAchievement.Type.TEN_LESSONS);
        }
        if (s.quizzesCompleted >= 1) {
            set.add(UserAchievement.Type.FIRST_QUIZ);
        }
        if (s.currentStreak >= 7) {
            set.add(UserAchievement.Type.SEVEN_DAY_STREAK);
        }
        if (courseCompleted) {
            set.add(UserAchievement.Type.FIRST_COURSE);
        }
        return set;
    }

    /** Saves each new achievement (the unique index blocks duplicates) and adds its points to the total. */
    private List<AchievementView> award(String userId, Set<UserAchievement.Type> candidates) {
        List<AchievementView> awarded = new ArrayList<>();
        int bonus = 0;

        for (UserAchievement.Type type : candidates) {
            if (achievementRepo.existsByUserIdAndAchievementId(userId, type.name())) {
                continue;
            }
            UserAchievement a = new UserAchievement();
            a.userId = userId;
            a.achievementId = type.name();
            a.title = type.title;
            a.points = type.points;
            try {
                achievementRepo.save(a);
            } catch (DuplicateKeyException e) {
                continue; // another request awarded it first
            }
            bonus += type.points;
            awarded.add(new AchievementView(type.name(), type.title, type.description, type.points, true, a.awardedAt));
        }

        if (bonus > 0) {
            final int add = bonus;
            mutateStats(userId, s -> s.totalPoints += add);
        }
        return awarded;
    }

    /**
     * Reads, changes and saves the student's stats. @Version makes a concurrent update fail instead of being
     * silently overwritten, and the change is then re-applied to the fresh copy.
     */
    private LearningStats mutateStats(String userId, Consumer<LearningStats> change) {
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                LearningStats s = statsRepo.findByUserId(userId).orElseGet(() -> emptyStats(userId));
                change.accept(s);
                s.updatedAt = Instant.now();
                return statsRepo.save(s);
            } catch (OptimisticLockingFailureException | DuplicateKeyException e) {
                // someone else saved first; loop and try again with the fresh copy
            }
        }
        throw new ApiException(HttpStatus.CONFLICT, "CONFLICT", "Too many simultaneous updates. Please try again.");
    }

    private static LearningStats emptyStats(String userId) {
        LearningStats s = new LearningStats();
        s.userId = userId;
        return s;
    }

    // ====================================================================================
    //  PROGRESS VIEWS
    // ====================================================================================

    public CourseProgressView courseProgress(String userId, String courseId) {
        return courseProgress(userId, content.requirePublishedCourse(courseId));
    }

    private CourseProgressView courseProgress(String userId, Course course) {
        Set<String> lessonIds = lessons.findByCourseIdAndPublishedTrue(course.id).stream()
                .map(l -> l.id).collect(Collectors.toSet());

        int done = 0;
        LessonProgress last = null;
        for (LessonProgress p : progress.findByUserIdAndCourseId(userId, course.id)) {
            if (!lessonIds.contains(p.lessonId)) {
                continue; // a lesson that was deleted or unpublished no longer counts
            }
            if (p.completed) {
                done++;
            }
            if (p.lastVisitedAt != null && (last == null || p.lastVisitedAt.isAfter(last.lastVisitedAt))) {
                last = p;
            }
        }
        int total = lessonIds.size();
        int percent = total == 0 ? 0 : done * 100 / total;
        return new CourseProgressView(course.id, course.title, total, done, percent,
                total > 0 && done == total,
                last != null ? last.lessonId : null,
                last != null ? last.lastVisitedAt : null);
    }

    public OverallProgress overall(String userId) {
        List<CourseProgressView> list = new ArrayList<>();
        int total = 0;
        int done = 0;
        for (Course course : courses.findByPublishedTrueOrderByOrderIndexAsc()) {
            CourseProgressView view = courseProgress(userId, course);
            list.add(view);
            total += view.totalLessons();
            done += view.completedLessons();
        }
        LearningStats s = statsRepo.findByUserId(userId).orElseGet(() -> emptyStats(userId));
        return new OverallProgress(total, done, total == 0 ? 0 : done * 100 / total,
                s.totalPoints, s.quizzesCompleted, list);
    }

    /** The lesson "Continue Learning" opens: the last one the student visited, or a starting point. */
    public ResumeView resume(String userId) {
        LessonProgress last = progress.findFirstByUserIdOrderByLastVisitedAtDesc(userId).orElse(null);
        if (last != null) {
            Lesson lesson = lessons.findById(last.lessonId).orElse(null);
            if (lesson != null && lesson.published) {
                Course course = courses.findById(lesson.courseId).orElse(null);
                if (course != null && course.published) {
                    return new ResumeView(true, course.id, course.title, lesson.moduleId,
                            lesson.id, lesson.title, last.lastVisitedAt);
                }
            }
        }
        // Never started (or the last lesson no longer exists): suggest the very first lesson.
        for (Course course : courses.findByPublishedTrueOrderByOrderIndexAsc()) {
            List<Lesson> ordered = content.orderedLessons(course.id);
            if (!ordered.isEmpty()) {
                Lesson first = ordered.get(0);
                return new ResumeView(false, course.id, course.title, first.moduleId, first.id, first.title, null);
            }
        }
        return new ResumeView(false, null, null, null, null, null, null);
    }

    public StreakView streak(String userId) {
        return streakView(statsRepo.findByUserId(userId).orElseGet(() -> emptyStats(userId)));
    }

    private StreakView streakView(LearningStats s) {
        LocalDate today = LocalDate.now(zone);
        boolean alive = s.lastActivityDate != null && !s.lastActivityDate.isBefore(today.minusDays(1));
        boolean activeToday = s.lastActivityDate != null && s.lastActivityDate.equals(today);
        return new StreakView(alive ? s.currentStreak : 0, s.longestStreak, s.lastActivityDate,
                s.totalLearningDays, activeToday);
    }

    /** Every achievement definition, marked as earned or not. */
    public List<AchievementView> achievements(String userId) {
        Map<String, UserAchievement> earned = new HashMap<>();
        for (UserAchievement a : achievementRepo.findByUserIdOrderByAwardedAtDesc(userId)) {
            earned.put(a.achievementId, a);
        }
        List<AchievementView> result = new ArrayList<>();
        for (UserAchievement.Type type : UserAchievement.Type.values()) {
            UserAchievement a = earned.get(type.name());
            result.add(new AchievementView(type.name(), type.title, type.description, type.points,
                    a != null, a != null ? a.awardedAt : null));
        }
        return result;
    }

    public List<ActivityItem> recentActivity(String userId) {
        List<ActivityItem> items = new ArrayList<>();

        List<LessonProgress> completed = progress.findTop10ByUserIdAndCompletedTrueOrderByCompletedAtDesc(userId);
        Map<String, Lesson> lessonById = new HashMap<>();
        lessons.findAllById(completed.stream().map(p -> p.lessonId).toList())
                .forEach(l -> lessonById.put(l.id, l));
        for (LessonProgress p : completed) {
            Lesson l = lessonById.get(p.lessonId);
            items.add(new ActivityItem("LESSON_COMPLETED",
                    l != null ? "Completed lesson: " + l.title : "Completed a lesson",
                    p.lessonId, LESSON_POINTS, p.completedAt));
        }
        for (QuizAttempt a : attempts.findTop10ByUserIdOrderByAttemptedAtDesc(userId)) {
            items.add(new ActivityItem("QUIZ_COMPLETED",
                    "Quiz: " + a.quizTitle + " (" + a.scorePercent + "%)", a.quizId, a.pointsAwarded, a.attemptedAt));
        }
        for (UserAchievement a : achievementRepo.findTop10ByUserIdOrderByAwardedAtDesc(userId)) {
            items.add(new ActivityItem("ACHIEVEMENT_EARNED",
                    "Achievement unlocked: " + a.title, a.achievementId, a.points, a.awardedAt));
        }

        items.sort(Comparator.comparing(ActivityItem::occurredAt).reversed());
        return items.size() > 10 ? new ArrayList<>(items.subList(0, 10)) : items;
    }

    /** Completed lessons plus quiz attempts for each of the last 7 learning days, oldest first. */
    public List<DayCount> weekly(String userId) {
        LocalDate today = LocalDate.now(zone);
        LocalDate start = today.minusDays(6);
        Instant since = start.atStartOfDay(zone).toInstant();

        Map<LocalDate, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            counts.put(start.plusDays(i), 0);
        }
        for (LessonProgress p : progress.findByUserIdAndCompletedTrueAndCompletedAtGreaterThanEqual(userId, since)) {
            counts.merge(p.completedAt.atZone(zone).toLocalDate(), 1, Integer::sum);
        }
        for (QuizAttempt a : attempts.findByUserIdAndAttemptedAtGreaterThanEqual(userId, since)) {
            counts.merge(a.attemptedAt.atZone(zone).toLocalDate(), 1, Integer::sum);
        }
        return counts.entrySet().stream().map(e -> new DayCount(e.getKey(), e.getValue())).toList();
    }

    public DashboardView dashboard(String userId) {
        String name = users.findById(userId).map(u -> u.name).orElse("Student");
        List<BookmarkView> allBookmarks = bookmarks(userId);
        List<AttemptSummary> recentQuizzes = attempts.findTop10ByUserIdOrderByAttemptedAtDesc(userId).stream()
                .map(QuizService::toAttemptSummary).toList();

        return new DashboardView(name, resume(userId), overall(userId), streak(userId),
                recentActivity(userId), recentQuizzes,
                allBookmarks.size() > 5 ? allBookmarks.subList(0, 5) : allBookmarks,
                achievements(userId), weekly(userId));
    }

    // ====================================================================================
    //  BOOKMARKS
    // ====================================================================================

    public List<BookmarkView> bookmarks(String userId) {
        List<Bookmark> list = bookmarkRepo.findByUserIdOrderByCreatedAtDesc(userId);

        Map<String, Lesson> lessonById = new HashMap<>();
        lessons.findAllById(list.stream().map(b -> b.lessonId).toList()).forEach(l -> lessonById.put(l.id, l));
        Set<String> courseIds = new HashSet<>();
        lessonById.values().forEach(l -> courseIds.add(l.courseId));
        Map<String, Course> courseById = new HashMap<>();
        courses.findAllById(courseIds).forEach(c -> courseById.put(c.id, c));

        List<BookmarkView> result = new ArrayList<>();
        for (Bookmark b : list) {
            Lesson lesson = lessonById.get(b.lessonId);
            if (lesson == null || !lesson.published) {
                continue; // the lesson was deleted or unpublished; the bookmark is hidden, not erased
            }
            Course course = courseById.get(lesson.courseId);
            result.add(new BookmarkView(lesson.id, lesson.title, lesson.courseId,
                    course != null ? course.title : "", b.createdAt));
        }
        return result;
    }

    public BookmarkView addBookmark(String userId, String lessonId) {
        Lesson lesson = content.requirePublishedLesson(lessonId);
        Course course = content.requireCourse(lesson.courseId);

        Bookmark existing = bookmarkRepo.findByUserIdAndLessonId(userId, lessonId).orElse(null);
        if (existing == null) {
            Bookmark b = new Bookmark();
            b.userId = userId;
            b.courseId = lesson.courseId;
            b.lessonId = lessonId;
            try {
                existing = bookmarkRepo.save(b);
            } catch (DuplicateKeyException e) {
                existing = bookmarkRepo.findByUserIdAndLessonId(userId, lessonId)
                        .orElseThrow(() -> ApiException.conflict("Could not save the bookmark. Please try again."));
            }
        }
        return new BookmarkView(lesson.id, lesson.title, course.id, course.title, existing.createdAt);
    }

    /** Removing a bookmark that does not exist is not an error, so retries are safe. */
    public void removeBookmark(String userId, String lessonId) {
        bookmarkRepo.deleteByUserIdAndLessonId(userId, lessonId);
    }
}