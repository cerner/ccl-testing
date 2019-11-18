package com.cerner.ccl.parser.text.data.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * Unit tests for {@link DocumentationParserSupport}.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentationParserSupportTest {
    private final DocumentationParserSupport support = new DocumentationParserSupport();

    /**
     * Test the determination of a comment closure.
     */
    @Test
    public void testIsCommentClose() {
        assertThat(support.isCommentClose(" */")).isTrue();
        assertThat(support.isCommentClose("/**")).isFalse();
    }

    /**
     * Determination of a comment closure on a {@code null} line should fail.
     */
    @Test
    public void testIsCommentCloseNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            support.isCommentClose(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }

    /**
     * Test the determination of a comment start.
     */
    @Test
    public void testIsCommentStart() {
        assertThat(support.isCommentStart("  /**")).isTrue();
        assertThat(support.isCommentStart(" */")).isFalse();
    }

    /**
     * Testing a {@code null} for a comment start should fail.
     */
    @Test
    public void testIsCommentStartNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            support.isCommentStart(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }

    /**
     * Test the determination of a line containing a tag opening.
     */
    @Test
    public void testIsTagOpen() {
        @SuppressWarnings("unchecked")
        final List<String> tags = new ArrayList<String>(
                (List<String>) Whitebox.getInternalState(support, "terminatingTags"));
        tags.add("@value");
        tags.add("@optional");
        tags.add("@codeSet");

        for (final String tag : tags) {
            final String line = "* " + tag + " and some documentation";
            assertThat(support.isTagOpen(line)).as("Not marked as a tag open: " + line).isTrue();
        }
    }

    /**
     * Testing a {@code null} line for a tag opening should fail.
     */
    @Test
    public void testIsTagOpenNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            support.isTagOpen(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }

    /**
     * Test the determination of whether or not the given line contains a terminating tag.
     */
    @Test
    public void testIsTerminatingTagOpen() {
        final List<String> tags = Whitebox.getInternalState(support, "terminatingTags");
        for (final String tag : tags) {
            assertThat(support.isTerminatingTagOpen("* " + tag + " and a little doc"))
                    .as("Not marked as terminating tag: " + tag).isTrue();
        }

        assertThat(support.isTerminatingTagOpen("* @value this is an enumerated value")).isFalse();
    }

    /**
     * Testing for a terminating tag in a {@code null} line should fail.
     */
    @Test
    public void testIsTerminatingTagOpenNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            support.isTerminatingTagOpen(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }

    /**
     * Test the normalization of a string.
     */
    @Test
    public void testNormalize() {
        assertThat(support.normalize("* @value to be normal   ")).isEqualTo("@value to be normal");
    }

    /**
     * A comment closure shouldn't be substring'ed during normalization.
     */
    @Test
    public void testNormalizeCommentClose() {
        assertThat(support.normalize("   */   ")).isEqualTo("*/");
    }

    /**
     * Normalization of a {@code null} line should fail.
     */
    @Test
    public void testNormalizeNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            support.normalize(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }
}
