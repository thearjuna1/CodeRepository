package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "quizzes")
public class Quiz {

    /** A multiple-choice question embedded in a quiz. */
    public static class Question {
        public String id;
        public String text;
        public List<String> options = new ArrayList<>();
        public int correctIndex;
        public String explanation;
    }

    @Id
    public String id;

    @Indexed
    public String courseId;

    @Indexed
    public String moduleId;

    /** Optional: set when the quiz belongs to one specific lesson. */
    @Indexed
    public String lessonId;

    public String title;

    public String description;

    public int passingPercent = 60;

    public boolean published = true;

    public List<Question> questions = new ArrayList<>();

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();
}