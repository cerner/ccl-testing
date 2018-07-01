package com.cerner.ccl.testing.maven.ccl.reports.common;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.cerner.ccl.testing.maven.ccl.reports.common.CoveredStatus;

/**
 * Unit tests for {@link CoveredStatus}.
 * 
 * @author Joshua Hyde
 * 
 */

public class CoveredStatusTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * All statuses should be resolvable by their own character representation.
     */
    @Test
    public void testForCharacterRepresentation() {
        for (CoveredStatus status : CoveredStatus.values())
            assertThat(status).isEqualTo(CoveredStatus.forCharacterRepresentation(status.getCharacterRepresentation()));
    }

    /**
     * Resolution of covered status by character representation should be case-sensitive to ensure that the proper
     * format of the XML is being followed.
     */
    @Test
    public void testForCharacterRepresentationCaseSensitive() {
        final String toSearch = StringUtils.swapCase(CoveredStatus.COVERED.getCharacterRepresentation());
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Unknown character representation: " + toSearch);
        CoveredStatus.forCharacterRepresentation(toSearch);
    }

    /**
     * If the given character representation is unknown, then the lookup should fail.
     */
    @Test
    public void testForCharacterRepresentationUnknown() {
        final String toSearch = getClass().getCanonicalName();
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Unknown character representation: " + toSearch);
        CoveredStatus.forCharacterRepresentation(toSearch);
    }
}
