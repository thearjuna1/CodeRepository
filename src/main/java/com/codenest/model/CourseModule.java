package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "modules")
@CompoundIndex(name = "course_slug_unique", def = "{'courseId': 1, 'slug': 1}", unique = true)
public class CourseModule {

    @Id
    public String id;

    public String courseId;

    public String slug;

    public String title;

    public String description;

    public int orderIndex;

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();
}