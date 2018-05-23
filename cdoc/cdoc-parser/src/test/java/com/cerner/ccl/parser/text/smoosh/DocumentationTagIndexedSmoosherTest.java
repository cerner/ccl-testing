package com.cerner.ccl.parser.text.smoosh;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.text.smoosh.internal.AbstractIndexedSmoosherUnitTest;

/**
 * Unit tests for {@link DocumentationTagIndexedSmoosher}.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationTagIndexedSmoosherTest
        extends AbstractIndexedSmoosherUnitTest<DocumentationTagIndexedSmoosher> {
    private final DocumentationTagIndexedSmoosher smoosher = new DocumentationTagIndexedSmoosher();

    /**
     * Test the determination of the smooshability of a line.
     */
    @Test
    public void testCanSmoosh() {
        assertThat(smoosher.canSmoosh("@returns")).isTrue();
        assertThat(smoosher.canSmoosh("@notAClosingTag")).isFalse();
    }

    @Override
    protected DocumentationTagIndexedSmoosher getSmoosher() {
        return smoosher;
    }
}
