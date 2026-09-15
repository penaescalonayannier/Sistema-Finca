package com.kynsoft.share.core.domain.exception;

import com.kynsoft.share.core.domain.response.ApiError;
import com.kynsoft.share.core.domain.response.ApiResponse;
import com.kynsoft.share.core.domain.response.ErrorField;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final int MAX_STACK_LINES = 20;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    // -------------------------------------------------------------------------
    // 4xx handlers (unchanged)
    // -------------------------------------------------------------------------

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpClientErrorException(HttpClientErrorException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiError apiError = new ApiError("HTTP Error: " + ex.getMessage(), null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler(CustomUnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomUnauthorizedException(CustomUnauthorizedException ex) {
        ApiError apiError = new ApiError("An unexpected null value was encountered.", null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(AuthenticateNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticateNotFoundException(AuthenticateNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(UserChangePasswordException.class)
    public ResponseEntity<ApiResponse<?>> handleUserChangePasswordException(UserChangePasswordException ex) {
        ApiError apiError = new ApiError(ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessNotFoundException(BusinessNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getBrokenRule().getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(DoctorIsNotPermisionNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(DoctorIsNotPermisionNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getBrokenRule().getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(jakarta.ws.rs.NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(jakarta.ws.rs.NotFoundException ex) {
        ApiError apiError = new ApiError(ex.getMessage(), null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UserEmailDifferentException.class)
    public ResponseEntity<ApiResponse<?>> handleUserEmailDifferentException(UserEmailDifferentException ex) {
        ApiError apiError = new ApiError(ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(555).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidOtpException(InvalidOtpException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessRuleValidationException(BusinessRuleValidationException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getBrokenRule().getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    /**
     * Handles Bean Validation errors raised by {@code @Valid @RequestBody} on controller methods.
     * <p>
     * Returns HTTP 400 with a per-field list of error messages wrapped in the standard
     * {@link ApiResponse#fail(ApiError)} envelope so clients get a consistent error shape.
     * <p>
     * This handler is purely additive: it only fires in services that have
     * {@code spring-boot-starter-validation} on the classpath and actually use {@code @Valid}.
     * Services without the validator never raise this exception, so they keep their
     * previous behavior.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        List<ErrorField> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorField(
                        fe.getField(),
                        fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid value"))
                .collect(Collectors.toList());

        ApiError apiError = new ApiError("Validation failed", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    // -------------------------------------------------------------------------
    // 500 handlers — with ErrorAlertEvent notification
    // -------------------------------------------------------------------------

    /**
     * Handles {@link NullPointerException} → HTTP 500.
     * Also publishes an {@link ErrorAlertEvent} so interested micros can send an alert email.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {

        log.error("NullPointerException at {}: {}", resolveEndpoint(request), ex.getMessage(), ex);

        ApiError apiError = new ApiError("An unexpected null value was encountered.", null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);

        publishErrorAlert(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    /**
     * Catches ALL unhandled {@link RuntimeException} (including MediatorException which wraps
     * real business errors that weren't handled elsewhere) → HTTP 500.
     * <p>
     * MediatorException wraps the real cause → we unwrap it to get the root exception class and
     * message so the alert email reflects the actual problem, not the wrapper.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        Throwable rootCause = unwrapCause(ex);
        String message = rootCause.getMessage() != null ? rootCause.getMessage() : ex.getMessage();

        // If the root cause is a BusinessRuleValidationException (e.g. wrapped in MediatorException),
        // return the actual business message with 400 instead of a generic 500.
        if (rootCause instanceof BusinessRuleValidationException ruleEx) {
            log.warn("BusinessRuleValidationException (wrapped) at {}: {}", resolveEndpoint(request), message);
            ApiError apiError = new ApiError(ruleEx.getStatus(), ruleEx.getMessage(),
                    List.of(ruleEx.getBrokenRule().getErrorField()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
        }

        // If the root cause is a BusinessException (e.g. wrapped in MediatorException),
        // return the actual business message with 400 instead of a generic 500.
        if (rootCause instanceof BusinessException businessEx) {
            log.warn("BusinessException (wrapped) at {}: {}", resolveEndpoint(request), message);
            ApiError apiError = new ApiError(businessEx.getStatus(), message, null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
        }

        log.error("Unhandled RuntimeException at {}: {}",
                resolveEndpoint(request), message, ex);

        ApiError apiError = new ApiError(
                "An unexpected internal server error occurred. Please try again later.", null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);

        publishErrorAlert(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // -------------------------------------------------------------------------
    // private helpers
    // -------------------------------------------------------------------------

    /**
     * Publishes an {@link ErrorAlertEvent}. Wrapped in try-catch so a broken notification
     * path NEVER affects the original HTTP error response.
     */
    private void publishErrorAlert(Throwable ex, HttpServletRequest request, int statusCode) {
        try {
            Throwable rootCause = unwrapCause(ex);

            String exceptionClass = rootCause.getClass().getSimpleName();
            String message        = rootCause.getMessage() != null ? rootCause.getMessage() : ex.getMessage();
            String stackTrace     = truncateStackTrace(ex, MAX_STACK_LINES);
            String endpoint       = resolveEndpoint(request);
            String timestamp      = LocalDateTime.now().toString();

            ErrorAlertEvent event = new ErrorAlertEvent(
                    this,
                    serviceName,
                    exceptionClass,
                    message,
                    stackTrace,
                    endpoint,
                    timestamp,
                    statusCode
            );
            eventPublisher.publishEvent(event);
        } catch (Exception publishError) {
            log.warn("Failed to publish ErrorAlertEvent (notification skipped): {}",
                    publishError.getMessage());
        }
    }

    /**
     * Unwraps {@code MediatorException} (or any single-level wrapper) to get the real root cause.
     * If no cause exists, returns the exception itself.
     */
    private Throwable unwrapCause(Throwable ex) {
        Throwable cause = ex.getCause();
        return (cause != null) ? cause : ex;
    }

    /** Returns {@code "METHOD /path"} e.g. {@code "POST /api/users"}. */
    private String resolveEndpoint(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        return request.getMethod() + " " + request.getRequestURI();
    }

    /**
     * Converts a throwable's stack trace to a string and returns at most {@code maxLines} lines.
     * This prevents enormous payloads when deep call stacks are involved.
     */
    private String truncateStackTrace(Throwable ex, int maxLines) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String[] lines = sw.toString().split("\n");
        return Arrays.stream(lines)
                .limit(maxLines)
                .collect(Collectors.joining("\n"));
    }
}
