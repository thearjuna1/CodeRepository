package com.codenest.controller;

import com.codenest.dto.Dtos.AuthResponse;
import com.codenest.dto.Dtos.ErrorResponse;
import com.codenest.dto.Dtos.LoginRequest;
import com.codenest.dto.Dtos.PasswordChangeRequest;
import com.codenest.dto.Dtos.ProfileUpdateRequest;
import com.codenest.dto.Dtos.RegisterRequest;
import com.codenest.dto.Dtos.UserView;
import com.codenest.exception.ApiException;
import com.codenest.security.JwtService;
import com.codenest.service.AuthService;
import com.codenest.service.AuthService.AuthResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService auth;
    private final JwtService jwt;
    private final String cookieName;
    private final String cookiePath;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(AuthService auth, JwtService jwt,
                          @Value("${codenest.cookie.name}") String cookieName,
                          @Value("${codenest.cookie.path}") String cookiePath,
                          @Value("${codenest.cookie.secure}") boolean cookieSecure,
                          @Value("${codenest.cookie.same-site}") String cookieSameSite) {
        this.auth = auth;
        this.jwt = jwt;
        this.cookieName = cookieName;
        this.cookiePath = cookiePath;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    // ============================ sessions ============================

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return session(HttpStatus.CREATED, auth.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return session(HttpStatus.OK, auth.login(request));
    }

    /**
     * Exchanges the refresh cookie for a new access token and a new (rotated) refresh cookie.
     * If the refresh token is no longer usable the cookie is cleared and the frontend must ask for a new login.
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        try {
            return session(HttpStatus.OK, auth.refresh(readRefreshCookie(request)));
        } catch (ApiException e) {
            if (e.getCode().startsWith("REFRESH_")) {
                return ResponseEntity.status(e.getStatus())
                        .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                        .header(HttpHeaders.CACHE_CONTROL, "no-store")
                        .body(new ErrorResponse(Instant.now(), e.getStatus().value(), e.getCode(),
                                e.getMessage(), null));
            }
            throw e;
        }
    }

    /** Learning progress is untouched: only the refresh token is revoked. */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        auth.logout(readRefreshCookie(request));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .build();
    }

    @GetMapping("/auth/me")
    public UserView me(Authentication authentication) {
        return auth.me(authentication.getName());
    }

    // ============================ profile ============================

    @GetMapping("/profile")
    public UserView profile(Authentication authentication) {
        return auth.me(authentication.getName());
    }

    @PutMapping("/profile")
    public UserView updateProfile(Authentication authentication, @Valid @RequestBody ProfileUpdateRequest request) {
        return auth.updateProfile(authentication.getName(), request);
    }

    /** Signs out every other session and keeps this one signed in with a fresh access token and cookie. */
    @PutMapping("/profile/password")
    public ResponseEntity<AuthResponse> changePassword(Authentication authentication,
                                                       @Valid @RequestBody PasswordChangeRequest request) {
        return session(HttpStatus.OK, auth.changePassword(authentication.getName(), request));
    }

    // ============================ cookie helpers ============================

    private ResponseEntity<AuthResponse> session(HttpStatus status, AuthResult result) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.refreshToken()).toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(result.body());
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(cookiePath)
                .maxAge(jwt.refreshTokenSeconds())
                .build();
    }

    private ResponseCookie clearCookie() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(cookiePath)
                .maxAge(0)
                .build();
    }

    private String readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}