package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
public class User {

    public enum Role { STUDENT, ADMIN }

    @Id
    public String id;

    public String name;

    @Indexed(unique = true)
    public String email;

    public String passwordHash;

    public Role role = Role.STUDENT;

    public boolean enabled = true;

    public Instant createdAt = Instant.now();

    public Instant updatedAt = Instant.now();
}