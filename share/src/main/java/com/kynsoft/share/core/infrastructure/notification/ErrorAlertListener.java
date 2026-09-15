package com.kynsoft.share.core.infrastructure.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.share.core.domain.exception.ErrorAlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listens for {@link ErrorAlertEvent} and publishes a notification to RabbitMQ.
 *
 * <p>Activation conditions:
 * <ul>
 *   <li>{@code RabbitTemplate} bean must be present in the application context
 *       (i.e. the micro must have spring-boot-starter-amqp on its classpath).</li>
 *   <li>Property {@code notification.error.enabled} must not be set to {@code false}
 *       (defaults to enabled).</li>
 * </ul>
 *
 * <p>Throttle: a 5-minute in-memory cooldown per unique key
 * {@code serviceName:exceptionClass:endpoint} prevents alert storms.
 */
@Component
@EnableScheduling
@ConditionalOnClass(RabbitTemplate.class)
@ConditionalOnBean(RabbitTemplate.class)
@ConditionalOnProperty(name = "notification.error.enabled", havingValue = "true", matchIfMissing = true)
public class ErrorAlertListener {

    private static final Logger log = LoggerFactory.getLogger(ErrorAlertListener.class);

    // ------------------------------------------------------------------ constants

    private static final String NOTIFICATION_EXCHANGE  = "notification.topic.exchange";
    private static final String ROUTING_KEY_ERROR      = "notification.error";
    private static final long   THROTTLE_MS            = 5 * 60 * 1000L; // 5 minutes
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ------------------------------------------------------------------ config

    @Value("${notification.template.error:}")
    private String errorTemplateId;

    @Value("${notification.error.recipient:admin@kynsoft.com}")
    private String errorRecipient;

    // ------------------------------------------------------------------ state

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper   objectMapper;

    /** Key → epoch-ms of last alert sent for that key (in-memory throttle). */
    private final ConcurrentHashMap<String, Long> recentAlerts = new ConcurrentHashMap<>();

    // ------------------------------------------------------------------ constructor

    public ErrorAlertListener(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper   = objectMapper;
    }

    // ------------------------------------------------------------------ listener

    @EventListener
    public void onErrorAlert(ErrorAlertEvent event) {
        String throttleKey = event.getServiceName() + ":" +
                             event.getExceptionClass() + ":" +
                             event.getEndpoint();

        long now = System.currentTimeMillis();
        Long lastSent = recentAlerts.get(throttleKey);

        if (lastSent != null && (now - lastSent) < THROTTLE_MS) {
            log.debug("ErrorAlert throttled for key '{}' (last sent {}ms ago)", throttleKey, now - lastSent);
            return;
        }

        recentAlerts.put(throttleKey, now);

        try {
            String json = buildNotificationJson(event);
            rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, ROUTING_KEY_ERROR, json);
            log.info("Published ErrorAlert for service='{}' exception='{}' endpoint='{}'",
                    event.getServiceName(), event.getExceptionClass(), event.getEndpoint());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ErrorAlert notification: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to publish ErrorAlert to RabbitMQ: {}", e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------ periodic cleanup

    /**
     * Removes stale entries from the throttle map every 5 minutes to prevent unbounded growth.
     */
    @Scheduled(fixedRate = 300_000)
    public void cleanupThrottle() {
        long now = System.currentTimeMillis();
        int before = recentAlerts.size();
        recentAlerts.entrySet().removeIf(e -> (now - e.getValue()) > THROTTLE_MS);
        int removed = before - recentAlerts.size();
        if (removed > 0) {
            log.debug("ErrorAlert throttle cleanup: removed {} stale entries", removed);
        }
    }

    // ------------------------------------------------------------------ private helpers

    private String buildNotificationJson(ErrorAlertEvent event) throws JsonProcessingException {
        String subject = "[" + event.getServiceName() + "] Error " + event.getStatusCode() +
                         ": " + event.getExceptionClass();

        String displayTimestamp = formatTimestamp(event.getOccurredAt());

        Map<String, String> variables = new HashMap<>();
        variables.put("service_name",    event.getServiceName());
        variables.put("endpoint",        event.getEndpoint());
        variables.put("exception_class", event.getExceptionClass());
        variables.put("error_message",   event.getMessage() != null ? event.getMessage() : "(no message)");
        variables.put("stack_trace",     event.getStackTrace() != null ? event.getStackTrace() : "");
        variables.put("timestamp",       displayTimestamp);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("source",  "error-alert");
        metadata.put("service", event.getServiceName());

        Map<String, Object> notification = new HashMap<>();
        notification.put("type",           "ERROR_ALERT");
        notification.put("recipientEmail", errorRecipient);
        notification.put("recipientName",  "Admin");
        notification.put("templateId",     errorTemplateId);
        notification.put("subject",        subject);
        notification.put("variables",      variables);
        notification.put("metadata",       metadata);

        return objectMapper.writeValueAsString(notification);
    }

    /**
     * Parses the ISO timestamp stored in the event and returns a human-readable string.
     * Falls back to the raw string if parsing fails.
     */
    private String formatTimestamp(String isoTimestamp) {
        if (isoTimestamp == null) {
            return LocalDateTime.now().format(DISPLAY_FMT);
        }
        try {
            return LocalDateTime.parse(isoTimestamp).format(DISPLAY_FMT);
        } catch (Exception e) {
            return isoTimestamp;
        }
    }
}
