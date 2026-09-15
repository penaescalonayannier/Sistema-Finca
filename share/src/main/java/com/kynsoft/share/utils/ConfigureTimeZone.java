package com.kynsoft.share.utils;

import java.time.*;
import java.util.Date;

public class ConfigureTimeZone {

    private static final String ZONE_ID = "America/Guayaquil";
    
    public static LocalDateTime getTimeZone() {
        // LocalDateTime is immutable: the old body called localDateTime.atZone(...) and threw
        // the result away, so this returned the JVM default zone, not ZONE_ID. It only looked
        // correct because the container sets TZ=America/Guayaquil. Every other copy of this
        // class in the monorepo already reads LocalDateTime.now(ZoneId.of(ZONE_ID)).
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of(ZONE_ID));
        
        return localDateTime;
    }
    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of(ZONE_ID));
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        return localDateTime;
    }

    public static boolean validateEqualsDate(LocalDateTime validate) {
        LocalDateTime today = ConfigureTimeZone.getTimeZone();
        LocalDate actual = today.toLocalDate();
        LocalDate save = validate.toLocalDate();

        return !save.equals(actual);
    }
}
