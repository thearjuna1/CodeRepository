package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "quiz_attempts")
@CompoundIndexes({
        @CompoundIndex(name = "user_quiz_date", def = "{'userId': 1, 'quizId': 1, 'attemptedAt': -1}"),
        @CompoundIndex(name = "user_client_attempt_unique",
                def = "{'userId': 1, 'clientAttemptId': 1}", unique = true, sparse = true)
})
public class QuizAttempt {

    /** Per-question result snapshot. */
    public static class AnswerResult {
        public String questionId;
        public String questionText;
        public int selectedIndex;
        public int correctIndex;
        public boolean correct;
        public String explanation;
    }

    @Id
    public String id;

    public String userId;

    public String quizId;

    public String quizTitle;

    public String clientAttemptId;

    public Instant attemptedAt = Instant.now();

    public int totalQuestions;

    public int correctAnswers;

    /** Whole-number percentage. */
    public int scorePercent;

    public boolean passed;

    public int pointsAwarded;

    public List<AnswerResult> answers = new ArrayList<>();
}