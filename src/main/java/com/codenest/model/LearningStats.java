package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "learning_stats")
public class LearningStats {

    @Id
    public String id;

    @Version
    public Long version;

    @Indexed(unique = true)
    public String userId;

    public int currentStreak;

    public int longestStreak;

    public LocalDate lastActivityDate;

    public int totalLearningDays;

    public int totalPoints;

    public int lessonsCompleted;

    public int quizzesCompleted;

    public Instant updatedAt = Instant.now();
}