package com.codenest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    public String id;

    @Indexed
    public String userId;

    @Indexed(unique = true)
    public String tokenHash;

    @Indexed
    public String familyId;

    @Indexed(expireAfterSeconds = 0)
    public Instant expiresAt;

    public boolean revoked;
    public Instant revokedAt;

    public Instant createdAt = Instant.now();
}