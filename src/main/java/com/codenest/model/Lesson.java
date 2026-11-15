package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "lessons")
@CompoundIndexes({
        @CompoundIndex(name = "course_slug_unique", def = "{'courseId': 1, 'slug': 1}", unique = true),
        @CompoundIndex(name = "module_order", def = "{'moduleId': 1, 'orderIndex': 1}")
})
public class Lesson {

    /** A code sample shown inside a lesson. */
    public static class CodeExample {
        public String title;
        public String code;
        public String expectedOutput;

        public CodeExample() {
        }

        public CodeExample(String title, String code, String expectedOutput) {
            this.title = title;
            this.code = code;
            this.expectedOutput = expectedOutput;
        }
    }

    @Id
    public String id;

    @Indexed
    public String courseId;

    public String moduleId;

    public String slug;

    public String title;

    public int orderIndex;

    public int estimatedMinutes = 10;

    public List<String> objectives = new ArrayList<>();

    public String explanation;

    public List<CodeExample> examples = new ArrayList<>();

    public List<String> importantNotes = new ArrayList<>();

    public List<String> commonMistakes = new ArrayList<>();

    public List<String> relatedTopics = new ArrayList<>();

    public String youtubeVideoId;

    public String videoTitle;

    public boolean published = true;

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();

}