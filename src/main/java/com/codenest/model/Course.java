package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "courses")
public class Course {

    @Id
    public String id;

    @Indexed(unique = true)
    public String slug;

    public String title;

    public String description;

    public String level;

    public int orderIndex;

    public boolean published = true;

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();
}