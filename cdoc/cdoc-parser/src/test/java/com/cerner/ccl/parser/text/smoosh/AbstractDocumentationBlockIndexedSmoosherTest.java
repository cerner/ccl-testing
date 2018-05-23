package com.cerner.ccl.parser.text.smoosh;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.text.smoosh.internal.AbstractIndexedSmoosherUnitTest;

/**
 * Unit tests for {@link AbstractDocumentationBlockIndexedSmoosher}.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractDocumentationBlockIndexedSmoosherTest
        extends AbstractIndexedSmoosherUnitTest<AbstractDocumentationBlockIndexedSmoosher> {
    private final ConcreteSmoosher smoosher = new ConcreteSmoosher();

    /**
     * If the block of text ends with a comment closure, then everything in the text block should be returned.
     */
    @Test
    public void testSmooshCommentClose() {
        final List<String> text = Arrays.asList("/**", " * one", " * two", " * three", "*/");
        assertThat(smoosher.smoosh(1, text)).isEqualTo("one two three");
        assertThat(smoosher.getEndingIndex()).isEqualTo(4);
    }

    /**
     * If an opening tag is encountered before the comment block closure, then only the text up until the tag should be
     * returned.
     */
    @Test
    public void testSmooshOpeningTag() {
        final List<String> text = Arrays.asList("/**", " * one", " * two", "@returns excluded", "*/");
        assertThat(smoosher.smoosh(1, text)).isEqualTo("one two");
        assertThat(smoosher.getEndingIndex()).isEqualTo(3);
    }

    /**
     * If the current line contains a comment closure, then only that line should be returned.
     */
    @Test
    public void testSmooshSingleLine() {
        final List<String> text = Arrays.asList("/**", " * text */");
        assertThat(smoosher.smoosh(1, text)).isEqualTo("text");
        assertThat(smoosher.getEndingIndex()).isEqualTo(1);
    }

    @Override
    protected AbstractDocumentationBlockIndexedSmoosher getSmoosher() {
        return smoosher;
    }

    /**
     * A concrete implementation of {@link AbstractDocumentationBlockIndexedSmoosher} for assistance in testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteSmoosher extends AbstractDocumentationBlockIndexedSmoosher {
        ConcreteSmoosher() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canSmoosh(final String line) {
            if (line == null) {
                throw new IllegalArgumentException("Line cannot be null.");
            }

            return true;
        }
    }
}
