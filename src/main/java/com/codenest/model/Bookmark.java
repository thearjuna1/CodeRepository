package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "bookmarks")
@CompoundIndex(name = "user_lesson_unique", def = "{'userId': 1, 'lessonId': 1}", unique = true)
public class Bookmark {

    @Id
    public String id;

    public String userId;

    public String courseId;

    public String lessonId;

    public Instant createdAt = Instant.now();
}