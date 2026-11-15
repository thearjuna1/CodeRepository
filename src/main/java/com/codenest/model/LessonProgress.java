package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "lesson_progress")
@CompoundIndexes({
        @CompoundIndex(name = "user_lesson_unique", def = "{'userId': 1, 'lessonId': 1}", unique = true),
        @CompoundIndex(name = "user_course", def = "{'userId': 1, 'courseId': 1}"),
        @CompoundIndex(name = "user_last_visited", def = "{'userId': 1, 'lastVisitedAt': -1}")
})
public class LessonProgress {

    @Id
    public String id;

    public String userId;

    public String courseId;

    public String moduleId;

    public String lessonId;

    public boolean completed;

    public Instant completedAt;

    public Instant firstVisitedAt;

    public Instant lastVisitedAt;

    /** Reading position as a fraction of the lesson body, 0.0 to 1.0. */
    public double readingPosition;

    /** Last known YouTube playback position in seconds (0 if never tracked). */
    public double videoPositionSeconds;

    /** READING or VIDEO: the mode the student last used. */
    public String lastMode = "READING";
}