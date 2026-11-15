package com.codenest.security;

import com.codenest.dto.Dtos.ErrorResponse;
import com.codenest.repository.Repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String CONTENT_SECURITY_POLICY = String.join("; ",
            "default-src 'self'",
            "script-src 'self' 'unsafe-inline' https://www.youtube.com https://s.ytimg.com",
            "style-src 'self' 'unsafe-inline'",
            "img-src 'self' data: https://i.ytimg.com",
            "frame-src https://www.youtube.com https://www.youtube-nocookie.com",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'self'",
            "frame-ancestors 'none'");

    private final List<String> allowedOrigins;

    public SecurityConfig(@Value("${codenest.cors.allowed-origins}") String originsCsv) {
        this.allowedOrigins = Arrays.stream(originsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Logins are handled by AuthService. This bean only stops Spring Boot from creating a default
     * in-memory user with a random password that it prints to the console.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Not used");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserRepository users,
            ObjectMapper mapper,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsSource
    ) throws Exception {

        // The filters are created here and not as @Component, so they are registered only once, inside this chain.
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtService, users);
        CookieEndpointGuard cookieGuard = new CookieEndpointGuard(Set.copyOf(allowedOrigins), mapper);

        http
                .csrf(AbstractHttpConfigurer::disable) // stateless Bearer API; cookie endpoints use CookieEndpointGuard
                .cors(cors -> cors.configurationSource(corsSource))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .contentSecurityPolicy(c -> c.policyDirectives(CONTENT_SECURITY_POLICY)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/register", "/api/auth/login",
                                "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String code = (String) request.getAttribute(JwtAuthFilter.ERROR_ATTRIBUTE);
                            if (code == null) {
                                code = "UNAUTHORIZED";
                            }
                            switch (code) {
                                case "TOKEN_EXPIRED" -> writeError(mapper, response, 401, code,
                                        "Your access token has expired.");
                                case "TOKEN_INVALID" -> writeError(mapper, response, 401, code,
                                        "Your session is not valid. Please sign in again.");
                                case "DATABASE_UNAVAILABLE" -> writeError(mapper, response, 503, code,
                                        "The database is temporarily unavailable. Your saved progress is safe; please try again shortly.");
                                default -> writeError(mapper, response, 401, "UNAUTHORIZED",
                                        "Authentication is required.");
                            }
                        })
                        .accessDeniedHandler((request, response, deniedException) ->
                                writeError(mapper, response, 403, "FORBIDDEN",
                                        "You do not have permission to do that.")))
                .addFilterBefore(cookieGuard, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ------------------------------------------------------------------------------------------

    private static void writeError(ObjectMapper mapper, HttpServletResponse response,
                                   int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        mapper.writeValue(response.getOutputStream(),
                new ErrorResponse(Instant.now(), status, code, message, null));
    }

    /**
     * CSRF defence for the two endpoints that rely on the refresh cookie (POST /api/auth/refresh and /logout).
     * A request must carry the header "X-Requested-With: codenest" (a cross-site page cannot add it without a
     * CORS preflight, which is refused), and its Origin, when present, must be same-origin or on the allow list.
     */
    private static class CookieEndpointGuard extends OncePerRequestFilter {

        private final Set<String> allowedOrigins;
        private final ObjectMapper mapper;

        CookieEndpointGuard(Set<String> allowedOrigins, ObjectMapper mapper) {
            this.allowedOrigins = allowedOrigins;
            this.mapper = mapper;
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getRequestURI();
            boolean cookieEndpoint = path.equals("/api/auth/refresh") || path.equals("/api/auth/logout");
            return !(cookieEndpoint && "POST".equalsIgnoreCase(request.getMethod()));
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {
            String origin = request.getHeader("Origin");
            String host = request.getHeader("Host");
            String sameOrigin = host == null ? null : request.getScheme() + "://" + host;

            boolean headerOk = "codenest".equals(request.getHeader("X-Requested-With"));
            boolean originOk = origin == null || origin.equals(sameOrigin) || allowedOrigins.contains(origin);

            if (!headerOk || !originOk) {
                writeError(mapper, response, 403, "CSRF_REJECTED", "The request was rejected.");
                return;
            }
            chain.doFilter(request, response);
        }
    }
}