package com.codenest.config;

import com.codenest.config.CurriculumData.LessonSeed;
import com.codenest.config.CurriculumData.ModuleSeed;
import com.codenest.config.CurriculumData.PracticeSeed;
import com.codenest.config.CurriculumData.QuestionSeed;
import com.codenest.config.CurriculumData.QuizSeed;
import com.codenest.model.Course;
import com.codenest.model.CourseModule;
import com.codenest.model.Lesson;
import com.codenest.model.PracticeQuestion;
import com.codenest.model.Quiz;
import com.codenest.model.User;
import com.codenest.repository.Repositories.CourseModuleRepository;
import com.codenest.repository.Repositories.CourseRepository;
import com.codenest.repository.Repositories.LessonRepository;
import com.codenest.repository.Repositories.PracticeQuestionRepository;
import com.codenest.repository.Repositories.QuizRepository;
import com.codenest.repository.Repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String SEED_KEY = "java-curriculum-v1";
    private static final String MARKER_COLLECTION = "seed_markers";

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final CourseRepository courses;
    private final CourseModuleRepository modules;
    private final LessonRepository lessons;
    private final QuizRepository quizzes;
    private final PracticeQuestionRepository practice;
    private final MongoTemplate mongo;
    private final boolean seedEnabled;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(UserRepository users, PasswordEncoder encoder, CourseRepository courses,
                           CourseModuleRepository modules, LessonRepository lessons, QuizRepository quizzes,
                           PracticeQuestionRepository practice, MongoTemplate mongo,
                           @Value("${codenest.seed.enabled}") boolean seedEnabled,
                           @Value("${codenest.admin.name}") String adminName,
                           @Value("${codenest.admin.email}") String adminEmail,
                           @Value("${codenest.admin.password}") String adminPassword) {
        this.users = users;
        this.encoder = encoder;
        this.courses = courses;
        this.modules = modules;
        this.lessons = lessons;
        this.quizzes = quizzes;
        this.practice = practice;
        this.mongo = mongo;
        this.seedEnabled = seedEnabled;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            createFirstAdmin();
        } catch (RuntimeException e) {
            log.error("First-administrator setup failed ({}). Is MongoDB running?", e.getClass().getSimpleName());
        }

        if (!seedEnabled) {
            log.info("Curriculum seeding is disabled (codenest.seed.enabled=false).");
            return;
        }
        try {
            seedCurriculum();
        } catch (RuntimeException e) {
            log.error("Curriculum seeding failed ({}). It will be retried on the next start.",
                    e.getClass().getSimpleName());
        }
    }


    private void createFirstAdmin() {
        String email = adminEmail == null ? "" : adminEmail.trim().toLowerCase(Locale.ROOT);
        if (email.isEmpty() || adminPassword == null || adminPassword.isBlank()) {
            log.info("No first administrator configured (set CODENEST_ADMIN_EMAIL and CODENEST_ADMIN_PASSWORD to create one).");
            return;
        }
        if (users.existsByRole(User.Role.ADMIN)) {
            log.info("An administrator already exists; the CODENEST_ADMIN_* settings are ignored.");
            return;
        }
        if (!email.contains("@") || email.length() > 120) {
            log.warn("CODENEST_ADMIN_EMAIL is not a valid email address; no administrator was created.");
            return;
        }
        if (adminPassword.length() < 8 || adminPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            log.warn("CODENEST_ADMIN_PASSWORD must be 8 to 72 characters; no administrator was created.");
            return;
        }
        if (users.existsByEmail(email)) {
            log.warn("An account with the administrator email already exists, so it was NOT promoted automatically. "
                    + "Use a different CODENEST_ADMIN_EMAIL, or change that account's role manually in MongoDB.");
            return;
        }

        User admin = new User();
        admin.name = adminName == null || adminName.isBlank() ? "CodeNest Admin" : adminName.trim();
        admin.email = email;
        admin.passwordHash = encoder.encode(adminPassword);
        admin.role = User.Role.ADMIN;
        users.save(admin);
        log.info("First administrator created: {}", email);
    }

    // ====================================================================================
    //  CURRICULUM
    // ====================================================================================

    private void seedCurriculum() {
        boolean alreadySeeded = mongo.exists(Query.query(Criteria.where("_id").is(SEED_KEY)), MARKER_COLLECTION);
        if (alreadySeeded) {
            return;
        }

        Course course = courses.findBySlug(CurriculumData.COURSE_SLUG).orElse(null);
        if (course == null) {
            course = new Course();
            course.slug = CurriculumData.COURSE_SLUG;
            course.title = CurriculumData.COURSE_TITLE;
            course.description = CurriculumData.COURSE_DESCRIPTION;
            course.level = CurriculumData.COURSE_LEVEL;
            course.orderIndex = 1;
            course = courses.save(course);
        }

        List<ModuleSeed> seeds = CurriculumData.modules();
        int lessonTotal = 0;

        for (int m = 0; m < seeds.size(); m++) {
            ModuleSeed moduleSeed = seeds.get(m);
            CourseModule module = ensureModule(course, moduleSeed, m + 1);

            for (int l = 0; l < moduleSeed.lessons().size(); l++) {
                ensureLesson(course, module, moduleSeed.lessons().get(l), l + 1);
                lessonTotal++;
            }
            if (moduleSeed.quiz() != null) {
                ensureQuiz(course, module, moduleSeed.quiz());
            }
            for (PracticeSeed p : moduleSeed.practice()) {
                ensurePractice(course, module, p);
            }
        }

        try {
            mongo.insert(new org.bson.Document("_id", SEED_KEY).append("seededAt", new Date()), MARKER_COLLECTION);
        } catch (DuplicateKeyException ignored) {
        }
        log.info("Curriculum ready: {} modules, {} lessons.", seeds.size(), lessonTotal);
    }

    private CourseModule ensureModule(Course course, ModuleSeed seed, int order) {
        String slug = slugify(seed.title());
        CourseModule existing = modules.findByCourseIdAndSlug(course.id, slug).orElse(null);
        if (existing != null) {
            return existing;
        }
        CourseModule module = new CourseModule();
        module.courseId = course.id;
        module.slug = slug;
        module.title = seed.title();
        module.description = seed.description();
        module.orderIndex = order;
        return modules.save(module);
    }

    private void ensureLesson(Course course, CourseModule module, LessonSeed seed, int order) {
        String slug = slugify(seed.title());
        if (lessons.findByCourseIdAndSlug(course.id, slug).isPresent()) {
            return;
        }
        Lesson lesson = new Lesson();
        lesson.courseId = course.id;
        lesson.moduleId = module.id;
        lesson.slug = slug;
        lesson.title = seed.title();
        lesson.orderIndex = order;
        lesson.estimatedMinutes = seed.minutes();
        lesson.objectives = new ArrayList<>(seed.objectives());
        lesson.explanation = seed.explanation().strip();
        lesson.examples = new ArrayList<>();
        for (Lesson.CodeExample e : seed.examples()) {
            lesson.examples.add(new Lesson.CodeExample(e.title, e.code.strip(), e.expectedOutput.strip()));
        }
        lesson.importantNotes = new ArrayList<>(seed.notes());
        lesson.commonMistakes = new ArrayList<>(seed.mistakes());
        lesson.relatedTopics = new ArrayList<>(seed.related());
        // No YouTube video is seeded: admins add real video links from the admin dashboard.
        lessons.save(lesson);
    }

    private void ensureQuiz(Course course, CourseModule module, QuizSeed seed) {
        boolean exists = quizzes.findByModuleIdAndPublishedTrue(module.id).stream()
                .anyMatch(q -> q.title.equals(seed.title()));
        if (exists) {
            return;
        }
        Quiz quiz = new Quiz();
        quiz.courseId = course.id;
        quiz.moduleId = module.id;
        quiz.title = seed.title();
        quiz.description = seed.description();
        quiz.passingPercent = 60;
        for (QuestionSeed qs : seed.questions()) {
            Quiz.Question q = new Quiz.Question();
            q.id = UUID.randomUUID().toString();
            q.text = qs.text();
            q.options = new ArrayList<>(qs.options());
            q.correctIndex = qs.correctIndex();
            q.explanation = qs.explanation();
            quiz.questions.add(q);
        }
        quizzes.save(quiz);
    }

    private void ensurePractice(Course course, CourseModule module, PracticeSeed seed) {
        boolean exists = practice.findByModuleIdAndPublishedTrue(module.id).stream()
                .anyMatch(p -> p.title.equals(seed.title()));
        if (exists) {
            return;
        }
        PracticeQuestion p = new PracticeQuestion();
        p.courseId = course.id;
        p.moduleId = module.id;
        p.title = seed.title();
        p.difficulty = seed.difficulty();
        p.description = seed.description();
        p.starterCode = seed.starterCode().strip();
        p.hint = seed.hint();
        p.referenceSolution = seed.solution().strip();
        p.expectedOutput = seed.expectedOutput().strip();
        practice.save(p);
    }

    private static String slugify(String text) {
        String slug = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+)|(-+$)", "");
        return slug.isEmpty() ? "item" : slug;
    }
}