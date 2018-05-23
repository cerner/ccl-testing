package com.cerner.ccl.parser.text.data.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * An object intended to support parsing of documentation.
 *
 * @author Joshua Hyde
 */

public class DocumentationParserSupport {
    private final List<String> terminatingTags = Arrays.asList("@field", "@returns", "@param", "@arg",
            "@boundTransaction", "@reply", "@request");

    /**
     * Determine whether or not the given line contains the closure of a comment.
     *
     * @param line
     *            The line of which it is to be determined whether or not it contains a comment closure.
     * @return {@code true} if the given line contains a comment closure; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    public boolean isCommentClose(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return line.contains("*/");
    }

    /**
     * Determine whether or not the given line is the start of a comment block.
     *
     * @param line
     *            The line to be determined whether or not it is the beginning of a comment block.
     * @return {@code true} if it is the start of a comment block; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    public boolean isCommentStart(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return line.trim().startsWith("/**");
    }

    /**
     * Determine whether or not the current line is the beginning of <i>any</i> documentation tag.
     *
     * @param line
     *            The line to be examined for a starting documentation tag.
     * @return {@code true} if it is the beginning of a new documentation tag; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     * @see #isTerminatingTagOpen(String)
     */
    public boolean isTagOpen(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        if (isTerminatingTagOpen(line)) {
            return true;
        }

        final String normalized = normalize(line);
        return normalized.startsWith("@value") || normalized.startsWith("@codeSet")
                || normalized.startsWith("@optional");
    }

    /**
     * Determine whether or not the given line contains a tag that terminates another tag (i.e., it contains a tag that
     * cannot be nested within another tag).
     *
     * @param line
     *            The line to be examined.
     * @return {@code true} if the given line contains a terminating tag; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     * @see #isTagOpen(String)
     */
    public boolean isTerminatingTagOpen(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        final String normalized = normalize(line);
        for (final String tag : terminatingTags) {
            if (normalized.startsWith(tag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Normalize a line via the following:
     * <ul>
     * <li>Strip trailing and leading whitespace</li>
     * <li>Remove a leading asterisk, if present</li>
     * </ul>
     * This is particularly useful for lines within a comment block preceded by an asterisk. The leading asterisk will
     * not be stripped if it is a comment block closure.
     *
     * @param line
     *            The line to be normalized.
     * @return The given line, normalized by the above-described criteria.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    public String normalize(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        final String stripped = StringUtils.strip(line);
        return StringUtils.strip(stripped.startsWith("*") && !isCommentClose(line) ? stripped.substring(1) : stripped);
    }

    /**
     * If a comment closure mark is in the given line, remove it.
     *
     * @param line
     *            The line to be stripped of comment closure marks.
     * @return The given string, less any comment closure marks.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    public String stripCommentClose(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return line.replaceAll("\\*/", "").trim();
    }
}
