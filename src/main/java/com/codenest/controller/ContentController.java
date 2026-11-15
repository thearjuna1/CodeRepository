package com.codenest.controller;

import com.codenest.dto.Dtos.CourseDetail;
import com.codenest.dto.Dtos.CourseProgressView;
import com.codenest.dto.Dtos.CourseSummary;
import com.codenest.dto.Dtos.LessonDetail;
import com.codenest.dto.Dtos.ModuleView;
import com.codenest.service.ContentService;
import com.codenest.service.ProgressService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService content;
    private final ProgressService progress;

    public ContentController(ContentService content, ProgressService progress) {
        this.content = content;
        this.progress = progress;
    }

    @GetMapping("/courses")
    public List<CourseSummary> courses() {
        return content.listCourses();
    }

    /** The full outline (modules and lessons), with a "completed" flag per lesson for the signed-in student. */
    @GetMapping("/courses/{courseId}")
    public CourseDetail course(Authentication authentication, @PathVariable String courseId) {
        return content.courseDetail(authentication.getName(), courseId);
    }

    @GetMapping("/courses/{courseId}/modules")
    public List<ModuleView> modules(Authentication authentication, @PathVariable String courseId) {
        return content.courseDetail(authentication.getName(), courseId).modules();
    }

    @GetMapping("/courses/{courseId}/progress")
    public CourseProgressView courseProgress(Authentication authentication, @PathVariable String courseId) {
        return progress.courseProgress(authentication.getName(), courseId);
    }

    /** Opening a lesson in the UI is followed by a PUT to /lessons/{id}/progress, which records it as last active. */
    @GetMapping("/lessons/{lessonId}")
    public LessonDetail lesson(Authentication authentication, @PathVariable String lessonId) {
        return content.lessonDetail(authentication.getName(), lessonId);
    }
}