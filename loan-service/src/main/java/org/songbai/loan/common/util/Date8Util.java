package org.songbai.loan.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 7:22 PM
 */
public class Date8Util {
    public static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter smdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();

    }

    public static Date LocalDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDate date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();

    }

    public static Date LocalDate2Date(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static String LocalDateTime2String(LocalDateTime dateTime) {
        return df.format(dateTime);
    }

    public static String LocalDateTime2SimpleString(LocalDateTime dateTime) {
        return smdf.format(dateTime);
    }

    public static String LocalDate2String(LocalDate dateTime) {
        return df.format(dateTime);
    }
}
