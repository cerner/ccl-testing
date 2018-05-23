package com.cerner.ccl.parser.text.documentation.parser;

import java.util.List;

import com.cerner.ccl.parser.text.data.util.DocumentationParserSupport;

/**
 * A parser driven by index-driven access of a list. This is ideal for when parsing subsections of a documentation
 * block. All implementations of this interface should be treated as stateful.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of object to be produced as a result of parsing.
 */

public abstract class AbstractIndexedParser<T> extends DocumentationParserSupport implements Parser {
    private int endingIndex = -1;

    /**
     * Parse a line.
     * 
     * @param startingIndex
     *            The index within the given list at which to start parsing.
     * @param lines
     *            A {@link List} of {@link String} objects that are to be parsed into an object.
     * @return A {@code T} object representing the parsed data.
     */
    public abstract T parse(int startingIndex, List<String> lines);

    /**
     * Get the last-examined index during {@link #parse(int, List) parsing}.
     * 
     * @return The last-examined index during parsing. If {@code -1}, then no line was ever actually examined by this
     *         object.
     */
    public int getEndingIndex() {
        return endingIndex;
    }

    /**
     * Set the last-examined index during parsing.
     * 
     * @param endingIndex
     *            The last-examined index.
     */
    protected void setEndingIndex(final int endingIndex) {
        this.endingIndex = endingIndex;
    }
}
