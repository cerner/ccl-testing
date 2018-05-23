package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

/**
 * Unit Tests for the CclUtils class
 *
 * @author Fred Eckertson
 *
 */
public class CclUtilsTest {

    /**
     * Test the converTimestamp method.
     */
    @Test
    public void testConvertTimestamp() {
        final Date date = CclUtils.convertTimestamp("2017-04-10T21:38:13.123-05:00");
        assertThat(date)
                .isEqualTo(new DateTime(2017, 04, 10, 21, 38, 13, 123, DateTimeZone.forID("America/Chicago")).toDate());
    }
}
