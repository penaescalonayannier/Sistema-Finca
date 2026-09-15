package com.kynsoft.share.core.domain.exception;

import org.springframework.context.ApplicationEvent;

/**
 * Spring ApplicationEvent published by GlobalExceptionHandler when a 500-level error occurs.
 * <p>
 * Design decision: share MUST NOT depend on spring-boot-starter-amqp directly.
 * Each microservice that wants RabbitMQ notification should register an
 * {@link org.springframework.context.event.EventListener} (or use the auto-configured
 * {@code ErrorAlertListener} in share's infrastructure.notification package).
 */
public class ErrorAlertEvent extends ApplicationEvent {

    private final String serviceName;
    private final String exceptionClass;
    private final String message;
    private final String stackTrace;
    private final String endpoint;
    private final String timestamp;
    private final int statusCode;

    public ErrorAlertEvent(
            Object source,
            String serviceName,
            String exceptionClass,
            String message,
            String stackTrace,
            String endpoint,
            String timestamp,
            int statusCode) {
        super(source);
        this.serviceName = serviceName;
        this.exceptionClass = exceptionClass;
        this.message = message;
        this.stackTrace = stackTrace;
        this.endpoint = endpoint;
        this.timestamp = timestamp;
        this.statusCode = statusCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getOccurredAt() {
        return timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
