package com.codenest.service;

import com.codenest.dto.Dtos.AuthResponse;
import com.codenest.dto.Dtos.LoginRequest;
import com.codenest.dto.Dtos.PasswordChangeRequest;
import com.codenest.dto.Dtos.ProfileUpdateRequest;
import com.codenest.dto.Dtos.RegisterRequest;
import com.codenest.dto.Dtos.UserView;
import com.codenest.exception.ApiException;
import com.codenest.model.RefreshToken;
import com.codenest.model.User;
import com.codenest.repository.Repositories.RefreshTokenRepository;
import com.codenest.repository.Repositories.UserRepository;
import com.codenest.security.JwtService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    /** The access-token response body plus the raw refresh token, which the controller puts in an HttpOnly cookie. */
    public record AuthResult(AuthResponse body, String refreshToken) {
    }

    /** A revoked token used again within this window is treated as a harmless race (e.g. two tabs). */
    private static final Duration REFRESH_GRACE = Duration.ofSeconds(10);

    private final UserRepository users;
    private final RefreshTokenRepository tokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final MongoTemplate mongo;
    private final String dummyHash;

    public AuthService(UserRepository users, RefreshTokenRepository tokens, PasswordEncoder encoder,
                       JwtService jwt, MongoTemplate mongo) {
        this.users = users;
        this.tokens = tokens;
        this.encoder = encoder;
        this.jwt = jwt;
        this.mongo = mongo;
        // Used so that logging in with an unknown email takes as long as a real login.
        this.dummyHash = encoder.encode("codenest-dummy-password");
    }

    // ============================ register / login ============================

    public AuthResult register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        requirePasswordLength(request.password());

        if (users.existsByEmail(email)) {
            throw ApiException.conflict("An account with this email already exists.");
        }

        User user = new User();
        user.name = request.name().trim();
        user.email = email;
        user.passwordHash = encoder.encode(request.password());
        user.role = User.Role.STUDENT; // public registration can never create an admin

        try {
            users.save(user);
        } catch (DuplicateKeyException e) {
            throw ApiException.conflict("An account with this email already exists.");
        }
        return issue(user, UUID.randomUUID().toString());
    }

    public AuthResult login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = users.findByEmail(email).orElse(null);

        boolean tooLong = request.password().getBytes(StandardCharsets.UTF_8).length > 72;
        boolean matches = !tooLong && encoder.matches(request.password(), user != null ? user.passwordHash : dummyHash);

        if (user == null || !matches || !user.enabled) {
            throw ApiException.unauthorized("INVALID_CREDENTIALS", "Incorrect email or password.");
        }
        return issue(user, UUID.randomUUID().toString());
    }

    // ============================ refresh / logout ============================

    /**
     * Rotates a refresh token. The old token is claimed with one atomic update (revoked: false to true),
     * so of two simultaneous requests with the same token only one can win.
     */
    public AuthResult refresh(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw ApiException.unauthorized("REFRESH_INVALID", "Please sign in again.");
        }
        String hash = JwtService.sha256(rawToken);

        Query unrevoked = new Query(Criteria.where("tokenHash").is(hash).and("revoked").is(false));
        Update revoke = new Update().set("revoked", true).set("revokedAt", Instant.now());
        RefreshToken claimed = mongo.findAndModify(unrevoked, revoke, RefreshToken.class);

        if (claimed != null) {
            if (claimed.expiresAt.isBefore(Instant.now())) {
                throw ApiException.unauthorized("REFRESH_EXPIRED", "Your session has expired. Please sign in again.");
            }
            return rotate(claimed);
        }

        RefreshToken existing = tokens.findByTokenHash(hash).orElse(null);
        if (existing == null) {
            throw ApiException.unauthorized("REFRESH_INVALID", "Please sign in again.");
        }

        boolean harmlessRace = existing.revokedAt != null
                && Duration.between(existing.revokedAt, Instant.now()).compareTo(REFRESH_GRACE) <= 0
                && existing.expiresAt.isAfter(Instant.now());
        if (harmlessRace) {
            return rotate(existing);
        }

        // A revoked token was presented again: treat it as possible theft and end the whole chain.
        revokeFamily(existing.familyId);
        throw ApiException.unauthorized("REFRESH_REUSED", "Your session has ended. Please sign in again.");
    }

    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }
        tokens.findByTokenHash(JwtService.sha256(rawToken)).ifPresent(t -> revokeFamily(t.familyId));
    }

    private AuthResult rotate(RefreshToken old) {
        User user = users.findById(old.userId).orElse(null);
        if (user == null || !user.enabled) {
            revokeFamily(old.familyId);
            throw ApiException.unauthorized("REFRESH_INVALID", "Please sign in again.");
        }
        return issue(user, old.familyId);
    }

    // ============================ profile ============================

    public UserView me(String userId) {
        return toView(requireUser(userId));
    }

    public UserView updateProfile(String userId, ProfileUpdateRequest request) {
        User user = requireUser(userId);
        user.name = request.name().trim();
        user.updatedAt = Instant.now();
        return toView(users.save(user));
    }

    /** Revokes every existing session and returns a fresh one so the current browser stays signed in. */
    public AuthResult changePassword(String userId, PasswordChangeRequest request) {
        User user = requireUser(userId);
        requirePasswordLength(request.newPassword());

        boolean tooLong = request.currentPassword().getBytes(StandardCharsets.UTF_8).length > 72;
        if (tooLong || !encoder.matches(request.currentPassword(), user.passwordHash)) {
            throw ApiException.badRequest("Your current password is incorrect.");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw ApiException.badRequest("Choose a new password that is different from the current one.");
        }

        user.passwordHash = encoder.encode(request.newPassword());
        user.updatedAt = Instant.now();
        users.save(user);

        mongo.updateMulti(
                new Query(Criteria.where("userId").is(userId).and("revoked").is(false)),
                new Update().set("revoked", true).set("revokedAt", Instant.now()),
                RefreshToken.class);

        return issue(user, UUID.randomUUID().toString());
    }

    // ============================ helpers ============================

    private AuthResult issue(User user, String familyId) {
        String raw = jwt.newRefreshTokenValue();

        RefreshToken token = new RefreshToken();
        token.userId = user.id;
        token.tokenHash = JwtService.sha256(raw);
        token.familyId = familyId;
        token.expiresAt = jwt.refreshExpiry();
        tokens.save(token);

        AuthResponse body = new AuthResponse(jwt.createAccessToken(user), jwt.accessTokenSeconds(), toView(user));
        return new AuthResult(body, raw);
    }

    private void revokeFamily(String familyId) {
        mongo.updateMulti(
                new Query(Criteria.where("familyId").is(familyId).and("revoked").is(false)),
                new Update().set("revoked", true).set("revokedAt", Instant.now()),
                RefreshToken.class);
    }

    private User requireUser(String userId) {
        return users.findById(userId)
                .orElseThrow(() -> ApiException.unauthorized("TOKEN_INVALID", "Please sign in again."));
    }

    private static void requirePasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw ApiException.badRequest("The password is too long (72 bytes maximum).");
        }
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static UserView toView(User user) {
        return new UserView(user.id, user.name, user.email, user.role.name(), user.createdAt);
    }
}