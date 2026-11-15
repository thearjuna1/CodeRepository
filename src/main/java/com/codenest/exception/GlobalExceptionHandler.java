package com.codenest.exception;

import com.codenest.dto.Dtos.ErrorResponse;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- our own exceptions ----------

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApi(ApiException ex) {
        return build(ex.getStatus().value(), ex.getCode(), ex.getMessage(), null);
    }

    // ---------- security ----------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(403, "FORBIDDEN", "You do not have permission to do that.", null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return build(401, "UNAUTHORIZED", "Authentication is required.", null);
    }

    // ---------- database ----------

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateKeyException ex) {
        return build(409, "CONFLICT", "That record already exists.", null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockingFailureException ex) {
        return build(409, "CONFLICT", "The data changed at the same moment. Please try again.", null);
    }

    @ExceptionHandler({DataAccessException.class, MongoException.class})
    public ResponseEntity<ErrorResponse> handleDatabase(Exception ex) {
        log.error("Database error: {}", ex.getClass().getSimpleName());
        return build(503, "DATABASE_UNAVAILABLE",
                "The database is temporarily unavailable. Your saved progress is safe; please try again shortly.", null);
    }

    // ---------- validation ----------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, "VALIDATION_FAILED",
                        "Please correct the highlighted fields.", fields));
    }

    // ---------- every other standard Spring MVC error (404, 405, 415, malformed JSON...) ----------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        int code = statusCode.value();
        String message = switch (code) {
            case 400 -> "The request was invalid.";
            case 404 -> "The requested resource was not found.";
            case 405 -> "That HTTP method is not allowed here.";
            case 415 -> "Unsupported content type.";
            default -> "The request could not be processed.";
        };
        String label = HttpStatus.resolve(code) != null ? HttpStatus.resolve(code).name() : "REQUEST_ERROR";
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponse(Instant.now(), code, label, message, null));
    }

    // ---------- last resort ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return build(500, "INTERNAL_ERROR", "Something went wrong on the server.", null);
    }

    private static ResponseEntity<ErrorResponse> build(int status, String code, String message,
                                                       Map<String, String> fieldErrors) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(Instant.now(), status, code, message, fieldErrors));
    }
}