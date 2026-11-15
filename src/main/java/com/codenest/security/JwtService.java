package com.codenest.security;

import com.codenest.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKey key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtService(@Value("${codenest.jwt.secret}") String secret,
                      @Value("${codenest.jwt.access-token-minutes}") long accessMinutes,
                      @Value("${codenest.jwt.refresh-token-days}") long refreshDays) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("codenest.jwt.secret must be at least 32 characters long.");
        }
        if (secret.startsWith("dev-only-secret")) {
            log.warn("Using the development JWT secret. Set CODENEST_JWT_SECRET before deploying anywhere real.");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    // ---------------- access tokens ----------------

    /** The subject is the user's stable MongoDB id. */
    public String createAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.id)
                .claim("role", user.role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
                .signWith(key)
                .compact();
    }

    /** Verifies the signature and expiry. Throws ExpiredJwtException or JwtException on failure. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long accessTokenSeconds() {
        return accessMinutes * 60;
    }

    // ---------------- refresh tokens ----------------

    /** A random, opaque value. It is not a JWT; the server only recognises it by its hash. */
    public String newRefreshTokenValue() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Instant refreshExpiry() {
        return Instant.now().plus(Duration.ofDays(refreshDays));
    }

    public long refreshTokenSeconds() {
        return refreshDays * 24 * 60 * 60;
    }

    /** SHA-256 hex digest. Only this hash is ever stored in MongoDB. */
    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}