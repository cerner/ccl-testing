package com.cerner.ccl.j4ccl.impl.util;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

/**
 * A set of utility methods to help bridge the gap between Java and CCL.
 *
 * @author Joshua Hyde
 *
 */

public class CclUtils {
    private static final DateTimeFormatter formatter;

    static {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        /*
         * Expected format: yyyy-MM-ddTHH:MM:ss.SZ
         */
        builder.appendYear(4, 4).appendLiteral('-').appendMonthOfYear(2).appendLiteral('-').appendDayOfMonth(2)
                .appendLiteral('T').appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':')
                .appendSecondOfMinute(2).appendLiteral('.').appendMillisOfSecond(1)
                .appendTimeZoneOffset(null, true, 2, 2);
        formatter = builder.toFormatter().withOffsetParsed();
    }

    /**
     * Convert a textual timestamp to a date object.
     *
     * @param timestamp
     *            The timestamp to be converted. The expected format is: {@code
     *            yyyy-MM-ddTHH:MM:ss.SZ}.
     *            <br>
     *            For example, the following is a valid timestamp: 2009-01-01T01:01:01.000-05:00
     * @return A {@link Date} object that matches the given timestamp.
     */
    public static Date convertTimestamp(final String timestamp) {
        return formatter.parseDateTime(timestamp).toDate();
    }
}