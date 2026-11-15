package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "practice_questions")
public class PracticeQuestion {

    @Id
    public String id;

    @Indexed
    public String courseId;

    @Indexed
    public String moduleId;

    @Indexed
    public String lessonId;

    public String title;

    public String description;

    /** EASY, MEDIUM or HARD. */
    public String difficulty = "EASY";

    public String starterCode;

    public String hint;

    public String referenceSolution;

    public String expectedOutput;

    public boolean published = true;

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();
}