package com.cerner.ccl.parser.text;

import java.util.List;

import com.cerner.ccl.parser.data.Described;

/**
 * A factory to produce objects representing top-level documentation of other objects.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of {@link Described} object to be produced.
 */

public interface TopLevelDocumentationFactory<T extends Described> {
    /**
     * Determine whether or not this factory can parse the source starting at the given line to produce its defined
     * object.
     * 
     * @param currentIndex
     *            The index on which to start parsing.
     * @param source
     *            A {@link List} of {@link String} objects representing the data to be parsed into a top-level
     *            documentation object.
     * @return {@code true} if this can parse the given source; {@code false} if not.
     */
    boolean canParse(int currentIndex, List<String> source);

    /**
     * Get the last index examined by this factory during {@link #parse(int, List) parsing}..
     * 
     * @return The last index examined during parsing.
     */
    int getEndingIndex();

    /**
     * Parse documentation into an object.
     * 
     * @param currentIndex
     *            The index from which parsing should start.
     * @param source
     *            A {@link List} of {@link String} objects representing the data to be parsed into a top-level
     *            documentation object.
     * @return A {@code T} object parsed out of the given source.
     */
    T parse(int currentIndex, List<String> source);
}
