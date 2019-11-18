package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link CodeSet}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class CodeSetTest extends AbstractBeanUnitTest<CodeSet> {
    private final int codeSetNumber = 113;
    private final String description = "I am a description";
    private final CodeSet codeSet = new CodeSet(codeSetNumber, description);

    /**
     * Construction with a negative code set value should fail.
     */
    @Test
    public void testConstructNegativeCodeSetNumber() {
        final int badNumber = -4389;
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CodeSet(badNumber, description);
        });
        assertThat(e.getMessage())
                .isEqualTo("Code set must be a non-zero, positive integer: " + Integer.toString(badNumber));
    }

    /**
     * Construction with no description should just make it an empty description.
     */
    @Test
    public void testConstructNoDescription() {
        assertThat(new CodeSet(codeSetNumber).getDescription()).isNotNull().isEmpty();
    }

    /**
     * Two code sets by different code set numbers must not be equal.
     */
    @Test
    public void testEqualsDifferentCodeSet() {
        assertThat(codeSet).isNotEqualTo(new CodeSet(codeSetNumber + 1, description));
    }

    /**
     * Only the code set should be used as a comparison; description should never come into play in determining
     * equality.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final CodeSet other = new CodeSet(codeSetNumber, StringUtils.reverse(description));
        assertThat(codeSet).isEqualTo(other);
        assertThat(other).isEqualTo(codeSet);

        assertThat(other.hashCode()).isEqualTo(codeSet.hashCode());
    }

    /**
     * Test the retrieval of the code set.
     */
    @Test
    public void testGetCodeSet() {
        assertThat(codeSet.getCodeSet()).isEqualTo(codeSetNumber);
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(codeSet.getDescription()).isEqualTo(description);
    }

    @Override
    protected CodeSet getBean() {
        return codeSet;
    }

    @Override
    protected CodeSet newBeanFrom(final CodeSet otherBean) {
        return new CodeSet(otherBean.getCodeSet(), otherBean.getDescription());
    }
}
