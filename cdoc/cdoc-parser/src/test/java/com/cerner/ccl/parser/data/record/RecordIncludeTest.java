package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link RecordInclude}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class RecordIncludeTest extends AbstractBeanUnitTest<RecordInclude> {
    private final String includeFilename = "cclsource:test.inc";
    private final RecordInclude include = new RecordInclude(includeFilename);

    /**
     * Construction with a {@code null} filename should fail.
     */
    @Test
    public void testConstructNullFilename() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new RecordInclude(null);
        });
        assertThat(e.getMessage()).isEqualTo("Filename cannot be null.");
    }

    /**
     * Two includes with different filenames should be inequal.
     */
    @Test
    public void testEqualsDifferentFilename() {
        final RecordInclude other = new RecordInclude(StringUtils.reverse(includeFilename));
        assertThat(include).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(include);
    }

    /**
     * Test the retrieval of the name of the object.
     */
    @Test
    public void testGetName() {
        assertThat(include.getName()).isEqualTo(includeFilename);
    }

    @Override
    protected RecordInclude getBean() {
        return include;
    }

    @Override
    protected RecordInclude newBeanFrom(final RecordInclude otherBean) {
        return new RecordInclude(otherBean.getName());
    }
}
