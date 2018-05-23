package com.cerner.ccl.parser.text.data.util;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Unit tests for {@link CaseInsensitiveComparator}.
 * 
 * @author Joshua Hyde
 * 
 */

public class CaseInsensitiveComparatorTest {
    private final CaseInsensitiveComparator comparator = CaseInsensitiveComparator.getInstance();

    /**
     * Test the case-insensitivity of the comparison.
     */
    @Test
    public void testCompare() {
        final String testString = "Q6HDKnxquKMn491ZSLy3";
        assertThat(comparator.compare(testString, StringUtils.swapCase(testString))).isZero();
    }
}
