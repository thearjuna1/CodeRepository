package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "user_achievements")
@CompoundIndex(name = "user_achievement_unique", def = "{'userId': 1, 'achievementId': 1}", unique = true)
public class UserAchievement {

    public enum Type {
        FIRST_LESSON("First Lesson Completed", "Complete your very first lesson.", 20),
        FIRST_QUIZ("First Quiz Completed", "Finish your first quiz.", 20),
        FIVE_LESSONS("Five Lessons Completed", "Complete five lessons.", 30),
        TEN_LESSONS("Ten Lessons Completed", "Complete ten lessons.", 50),
        SEVEN_DAY_STREAK("Seven-Day Streak", "Learn on seven days in a row.", 70),
        FIRST_COURSE("First Course Completed", "Complete every lesson in a course.", 150);

        public final String title;
        public final String description;
        public final int points;

        Type(String title, String description, int points) {
            this.title = title;
            this.description = description;
            this.points = points;
        }
    }

    @Id
    public String id;

    public String userId;

    /** Name of a Type constant, e.g. FIRST_LESSON. */
    public String achievementId;

    public String title;

    public int points;

    public Instant awardedAt = Instant.now();
}