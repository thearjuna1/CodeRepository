package com.codenest.service;

import com.codenest.dto.Dtos.AdminStats;
import com.codenest.dto.Dtos.CodeExampleRequest;
import com.codenest.dto.Dtos.CourseDetail;
import com.codenest.dto.Dtos.CourseRequest;
import com.codenest.dto.Dtos.CourseSummary;
import com.codenest.dto.Dtos.LessonDetail;
import com.codenest.dto.Dtos.LessonRequest;
import com.codenest.dto.Dtos.LessonSummary;
import com.codenest.dto.Dtos.ModuleRequest;
import com.codenest.dto.Dtos.ModuleView;
import com.codenest.exception.ApiException;
import com.codenest.model.Course;
import com.codenest.model.CourseModule;
import com.codenest.model.Lesson;
import com.codenest.model.LessonProgress;
import com.codenest.model.User;
import com.codenest.repository.Repositories.BookmarkRepository;
import com.codenest.repository.Repositories.CourseModuleRepository;
import com.codenest.repository.Repositories.CourseRepository;
import com.codenest.repository.Repositories.LessonProgressRepository;
import com.codenest.repository.Repositories.LessonRepository;
import com.codenest.repository.Repositories.PracticeQuestionRepository;
import com.codenest.repository.Repositories.QuizAttemptRepository;
import com.codenest.repository.Repositories.QuizRepository;
import com.codenest.repository.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.text.Normalizer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private static final Pattern VIDEO_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");

    private final CourseRepository courses;
    private final CourseModuleRepository modules;
    private final LessonRepository lessons;
    private final LessonProgressRepository progress;
    private final BookmarkRepository bookmarks;
    private final QuizRepository quizzes;
    private final PracticeQuestionRepository practice;
    private final UserRepository users;
    private final QuizAttemptRepository attempts;

    public ContentService(CourseRepository courses, CourseModuleRepository modules, LessonRepository lessons,
                          LessonProgressRepository progress, BookmarkRepository bookmarks,
                          QuizRepository quizzes, PracticeQuestionRepository practice,
                          UserRepository users, QuizAttemptRepository attempts) {
        this.courses = courses;
        this.modules = modules;
        this.lessons = lessons;
        this.progress = progress;
        this.bookmarks = bookmarks;
        this.quizzes = quizzes;
        this.practice = practice;
        this.users = users;
        this.attempts = attempts;
    }

    // ====================================================================================
    //  STUDENT READING
    // ====================================================================================

    public List<CourseSummary> listCourses() {
        List<CourseSummary> result = new ArrayList<>();
        for (Course c : courses.findByPublishedTrueOrderByOrderIndexAsc()) {
            result.add(new CourseSummary(c.id, c.title, c.description, c.level,
                    (int) modules.countByCourseId(c.id),
                    (int) lessons.countByCourseIdAndPublishedTrue(c.id)));
        }
        return result;
    }

    public CourseDetail courseDetail(String userId, String courseId) {
        Course course = requirePublishedCourse(courseId);
        List<CourseModule> moduleList = modules.findByCourseIdOrderByOrderIndexAsc(courseId);
        Map<String, List<Lesson>> byModule = lessons.findByCourseIdAndPublishedTrue(courseId).stream()
                .collect(Collectors.groupingBy(l -> l.moduleId));

        Set<String> done = new HashSet<>();
        for (LessonProgress p : progress.findByUserIdAndCourseId(userId, courseId)) {
            if (p.completed) {
                done.add(p.lessonId);
            }
        }

        List<ModuleView> views = new ArrayList<>();
        int total = 0;
        for (CourseModule m : moduleList) {
            List<LessonSummary> summaries = byModule.getOrDefault(m.id, List.of()).stream()
                    .sorted(Comparator.comparingInt((Lesson l) -> l.orderIndex))
                    .map(l -> new LessonSummary(l.id, l.title, l.orderIndex, l.estimatedMinutes,
                            l.youtubeVideoId != null && !l.youtubeVideoId.isBlank(), done.contains(l.id)))
                    .toList();
            total += summaries.size();
            views.add(new ModuleView(m.id, m.title, m.description, m.orderIndex, summaries));
        }
        return new CourseDetail(course.id, course.title, course.description, course.level, total, views);
    }

    public LessonDetail lessonDetail(String userId, String lessonId) {
        Lesson lesson = requirePublishedLesson(lessonId);
        Course course = requirePublishedCourse(lesson.courseId);
        CourseModule module = modules.findById(lesson.moduleId)
                .orElseThrow(() -> ApiException.notFound("Module not found."));

        List<Lesson> ordered = orderedLessons(course.id);
        int index = -1;
        for (int i = 0; i < ordered.size(); i++) {
            if (ordered.get(i).id.equals(lesson.id)) {
                index = i;
                break;
            }
        }
        String previous = index > 0 ? ordered.get(index - 1).id : null;
        String next = index >= 0 && index < ordered.size() - 1 ? ordered.get(index + 1).id : null;

        LessonProgress p = progress.findByUserIdAndLessonId(userId, lessonId).orElse(null);
        boolean completed = p != null && p.completed;
        double reading = p != null ? p.readingPosition : 0;
        double video = p != null ? p.videoPositionSeconds : 0;
        String mode = p != null && p.lastMode != null ? p.lastMode : "READING";

        return new LessonDetail(lesson.id, course.id, course.title, module.id, module.title,
                lesson.title, lesson.estimatedMinutes,
                nz(lesson.objectives), lesson.explanation, nz(lesson.examples),
                nz(lesson.importantNotes), nz(lesson.commonMistakes), nz(lesson.relatedTopics),
                lesson.youtubeVideoId, lesson.videoTitle,
                previous, next, completed, bookmarks.existsByUserIdAndLessonId(userId, lessonId),
                reading, video, mode);
    }

    /** Published lessons of a course in learning order: module order first, then lesson order. */
    public List<Lesson> orderedLessons(String courseId) {
        Map<String, Integer> moduleOrder = new HashMap<>();
        for (CourseModule m : modules.findByCourseIdOrderByOrderIndexAsc(courseId)) {
            moduleOrder.put(m.id, m.orderIndex);
        }
        return lessons.findByCourseIdAndPublishedTrue(courseId).stream()
                .filter(l -> moduleOrder.containsKey(l.moduleId))
                .sorted(Comparator.comparingInt((Lesson l) -> moduleOrder.get(l.moduleId))
                        .thenComparing((Lesson l) -> l.moduleId)
                        .thenComparingInt(l -> l.orderIndex))
                .toList();
    }

    // ---- lookups shared with the other services ----

    public Course requireCourse(String id) {
        return courses.findById(id).orElseThrow(() -> ApiException.notFound("Course not found."));
    }

    public Course requirePublishedCourse(String id) {
        Course course = requireCourse(id);
        if (!course.published) {
            throw ApiException.notFound("Course not found.");
        }
        return course;
    }

    public Lesson requireLesson(String id) {
        return lessons.findById(id).orElseThrow(() -> ApiException.notFound("Lesson not found."));
    }

    public Lesson requirePublishedLesson(String id) {
        Lesson lesson = requireLesson(id);
        if (!lesson.published) {
            throw ApiException.notFound("Lesson not found.");
        }
        return lesson;
    }

    // ====================================================================================
    //  ADMIN: COURSES
    // ====================================================================================

    public List<Course> adminCourses() {
        return courses.findAllByOrderByOrderIndexAsc();
    }

    public Course createCourse(CourseRequest r) {
        Course course = new Course();
        course.slug = uniqueSlug(slugify(r.title()), courses::existsBySlug);
        applyCourse(course, r);
        return courses.save(course);
    }

    public Course updateCourse(String id, CourseRequest r) {
        Course course = requireCourse(id);
        applyCourse(course, r);
        return courses.save(course);
    }

    /** Removes the course and its content. Students' saved progress records are deliberately left alone. */
    public void deleteCourse(String id) {
        requireCourse(id);
        lessons.deleteByCourseId(id);
        modules.deleteByCourseId(id);
        quizzes.deleteByCourseId(id);
        practice.deleteByCourseId(id);
        courses.deleteById(id);
    }

    private void applyCourse(Course course, CourseRequest r) {
        course.title = r.title().trim();
        course.description = trimOrNull(r.description());
        course.level = trimOrNull(r.level());
        course.orderIndex = r.orderIndex();
        if (r.published() != null) {
            course.published = r.published();
        }
        course.updatedAt = Instant.now();
    }

    // ====================================================================================
    //  ADMIN: MODULES
    // ====================================================================================

    public List<CourseModule> adminModules(String courseId) {
        requireCourse(courseId);
        return modules.findByCourseIdOrderByOrderIndexAsc(courseId);
    }

    public CourseModule createModule(ModuleRequest r) {
        requireCourse(r.courseId());
        CourseModule module = new CourseModule();
        module.courseId = r.courseId();
        module.slug = uniqueSlug(slugify(r.title()), s -> modules.existsByCourseIdAndSlug(r.courseId(), s));
        applyModule(module, r);
        return modules.save(module);
    }

    /** The course of an existing module cannot be changed; only its title, description and order. */
    public CourseModule updateModule(String id, ModuleRequest r) {
        CourseModule module = modules.findById(id).orElseThrow(() -> ApiException.notFound("Module not found."));
        if (!module.courseId.equals(r.courseId())) {
            throw ApiException.badRequest("A module cannot be moved to another course.");
        }
        applyModule(module, r);
        return modules.save(module);
    }

    public void deleteModule(String id) {
        modules.findById(id).orElseThrow(() -> ApiException.notFound("Module not found."));
        lessons.deleteByModuleId(id);
        quizzes.deleteByModuleId(id);
        practice.deleteByModuleId(id);
        modules.deleteById(id);
    }

    private void applyModule(CourseModule module, ModuleRequest r) {
        module.title = r.title().trim();
        module.description = trimOrNull(r.description());
        module.orderIndex = r.orderIndex();
        module.updatedAt = Instant.now();
    }

    // ====================================================================================
    //  ADMIN: LESSONS
    // ====================================================================================

    /** Includes unpublished lessons. */
    public List<Lesson> adminLessons(String courseId) {
        requireCourse(courseId);
        return lessons.findByCourseId(courseId).stream()
                .sorted(Comparator.comparingInt((Lesson l) -> l.orderIndex))
                .toList();
    }

    public Lesson adminGetLesson(String id) {
        return requireLesson(id);
    }

    public Lesson createLesson(LessonRequest r) {
        requireCourse(r.courseId());
        requireModuleInCourse(r.moduleId(), r.courseId());
        Lesson lesson = new Lesson();
        lesson.courseId = r.courseId();
        lesson.slug = uniqueSlug(slugify(r.title()), s -> lessons.existsByCourseIdAndSlug(r.courseId(), s));
        applyLesson(lesson, r);
        return lessons.save(lesson);
    }

    public Lesson updateLesson(String id, LessonRequest r) {
        Lesson lesson = requireLesson(id);
        if (!lesson.courseId.equals(r.courseId())) {
            throw ApiException.badRequest("A lesson cannot be moved to another course.");
        }
        requireModuleInCourse(r.moduleId(), lesson.courseId);
        applyLesson(lesson, r);
        return lessons.save(lesson);
    }

    public void deleteLesson(String id) {
        requireLesson(id);
        quizzes.deleteByLessonId(id);
        practice.deleteByLessonId(id);
        lessons.deleteById(id);
    }

    private void requireModuleInCourse(String moduleId, String courseId) {
        CourseModule module = modules.findById(moduleId)
                .orElseThrow(() -> ApiException.badRequest("The selected module does not exist."));
        if (!module.courseId.equals(courseId)) {
            throw ApiException.badRequest("The selected module belongs to a different course.");
        }
    }

    private void applyLesson(Lesson lesson, LessonRequest r) {
        lesson.moduleId = r.moduleId();
        lesson.title = r.title().trim();
        lesson.orderIndex = r.orderIndex();
        lesson.estimatedMinutes = r.estimatedMinutes() != null ? r.estimatedMinutes() : 10;
        lesson.objectives = cleanList(r.objectives());
        lesson.explanation = r.explanation().trim();
        lesson.examples = mapExamples(r.examples());
        lesson.importantNotes = cleanList(r.importantNotes());
        lesson.commonMistakes = cleanList(r.commonMistakes());
        lesson.relatedTopics = cleanList(r.relatedTopics());
        lesson.youtubeVideoId = extractYoutubeId(r.youtubeUrl());
        lesson.videoTitle = trimOrNull(r.videoTitle());
        if (r.published() != null) {
            lesson.published = r.published();
        }
        lesson.updatedAt = Instant.now();
    }

    private static List<Lesson.CodeExample> mapExamples(List<CodeExampleRequest> requests) {
        List<Lesson.CodeExample> result = new ArrayList<>();
        if (requests != null) {
            for (CodeExampleRequest e : requests) {
                result.add(new Lesson.CodeExample(trimOrNull(e.title()), e.code(), trimOrNull(e.expectedOutput())));
            }
        }
        return result;
    }

    // ====================================================================================
    //  ADMIN: STATISTICS
    // ====================================================================================

    public AdminStats adminStats() {
        return new AdminStats(
                users.count(),
                users.countByRole(User.Role.STUDENT),
                users.countByRole(User.Role.ADMIN),
                courses.count(),
                modules.count(),
                lessons.count(),
                quizzes.count(),
                practice.count(),
                attempts.count(),
                progress.countByCompletedTrue());
    }

    // ====================================================================================
    //  HELPERS (package-private so the other services can reuse them)
    // ====================================================================================

    static <T> List<T> nz(List<T> list) {
        return list == null ? List.of() : list;
    }

    static String trimOrNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    static List<String> cleanList(List<String> values) {
        List<String> result = new ArrayList<>();
        if (values != null) {
            for (String v : values) {
                String t = trimOrNull(v);
                if (t != null) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    static String slugify(String text) {
        String slug = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+)|(-+$)", "");
        return slug.isEmpty() ? "item" : slug;
    }

    static String uniqueSlug(String base, Predicate<String> exists) {
        String candidate = base;
        int counter = 2;
        while (exists.test(candidate)) {
            candidate = base + "-" + counter++;
        }
        return candidate;
    }

    /**
     * Accepts a bare 11-character video ID or a YouTube URL (watch, youtu.be, embed, shorts, live) and returns
     * only the validated ID. Anything else is rejected, so nothing other than a YouTube video can be embedded.
     */
    static String extractYoutubeId(String input) {
        String value = trimOrNull(input);
        if (value == null) {
            return null;
        }
        if (VIDEO_ID.matcher(value).matches()) {
            return value;
        }
        try {
            URI uri = URI.create(value.startsWith("http") ? value : "https://" + value);
            String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
            String path = uri.getPath() == null ? "" : uri.getPath();
            String candidate = null;

            if (host.equals("youtu.be")) {
                candidate = segment(path, 0);
            } else if (host.equals("youtube.com") || host.endsWith(".youtube.com")
                    || host.equals("youtube-nocookie.com") || host.endsWith(".youtube-nocookie.com")) {
                if (path.startsWith("/watch")) {
                    candidate = queryParam(uri.getRawQuery(), "v");
                } else if (path.startsWith("/embed/") || path.startsWith("/shorts/") || path.startsWith("/live/")) {
                    candidate = segment(path, 1);
                }
            }
            if (candidate != null && VIDEO_ID.matcher(candidate).matches()) {
                return candidate;
            }
        } catch (IllegalArgumentException ignored) {
            // falls through to the error below
        }
        throw ApiException.badRequest("That is not a valid YouTube link or video ID.");
    }

    private static String segment(String path, int index) {
        String[] parts = path.split("/");
        int seen = -1;
        for (String part : parts) {
            if (!part.isEmpty()) {
                seen++;
                if (seen == index) {
                    return part;
                }
            }
        }
        return null;
    }

    private static String queryParam(String query, String name) {
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            if (pair.startsWith(name + "=")) {
                return pair.substring(name.length() + 1);
            }
        }
        return null;
    }
}