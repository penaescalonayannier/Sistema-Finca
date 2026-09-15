package com.kynsoft.share.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ConfigureTimeZone#getTimeZone()} must answer in ZONE_ID no matter what the JVM
 * default zone is.
 *
 * <p>It used to call {@code localDateTime.atZone(...)} and discard the result. LocalDateTime is
 * immutable, so the conversion was a no-op and the method returned the JVM default zone. In
 * production that is invisible because the container sets TZ=America/Guayaquil; on a UTC CI
 * runner the two disagree, and between 00:00 and 05:00 UTC they land on different calendar
 * days, which broke every date-equality business rule built on this method.
 */
class ConfigureTimeZoneTest {

    private static final ZoneId ZONE = ZoneId.of("America/Guayaquil");
    private static final long TOLERANCE_SECONDS = 5;

    private final TimeZone original = TimeZone.getDefault();

    @AfterEach
    void restoreDefaultZone() {
        TimeZone.setDefault(original);
    }

    @Test
    @DisplayName("getTimeZone answers in ZONE_ID even when the JVM default is UTC")
    void getTimeZoneIgnoresTheJvmDefault() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        LocalDateTime actual = ConfigureTimeZone.getTimeZone();
        LocalDateTime expected = LocalDateTime.now(ZONE);

        assertClose(expected, actual,
                "getTimeZone() must return Guayaquil time, not the JVM default zone");
    }

    @Test
    @DisplayName("getTimeZone answers the same regardless of the JVM default")
    void getTimeZoneIsStableAcrossJvmDefaults() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        LocalDateTime underUtc = ConfigureTimeZone.getTimeZone();

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
        LocalDateTime underTokyo = ConfigureTimeZone.getTimeZone();

        assertClose(underUtc, underTokyo,
                "the JVM default zone must not shift the answer");
    }

    private static void assertClose(LocalDateTime expected, LocalDateTime actual, String message) {
        long drift = Math.abs(Duration.between(expected, actual).toSeconds());
        assertTrue(drift <= TOLERANCE_SECONDS,
                message + " — expected " + expected + " but was " + actual
                        + " (" + drift + "s apart)");
    }
}
