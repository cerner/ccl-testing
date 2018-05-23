package com.cerner.ccl.parser.text.data.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.cerner.ccl.parser.AbstractUnitTest;

/**
 * Unit tests for {@link DocumentationParserSupport}.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentationParserSupportTest extends AbstractUnitTest {
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
        expectNullLineFailure();
        support.isCommentClose(null);
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
        expectNullLineFailure();
        support.isCommentStart(null);
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
        expectNullLineFailure();
        support.isTagOpen(null);
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
        expectNullLineFailure();
        support.isTerminatingTagOpen(null);
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
        expectNullLineFailure();
        support.normalize(null);
    }

    /**
     * Set the expectation of the throwing of an {@link IllegalArgumentException} with the message "Line cannot be
     * null."
     */
    private void expectNullLineFailure() {
        expect(IllegalArgumentException.class);
        expect("Line cannot be null.");
    }
}
