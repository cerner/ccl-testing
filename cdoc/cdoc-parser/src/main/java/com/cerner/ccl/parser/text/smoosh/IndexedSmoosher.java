package com.cerner.ccl.parser.text.smoosh;

import java.util.List;

/**
 * An index-driven smoosher. An instance of this object should be treated as stateful.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of object to be produced as a result of smooshing.
 */

public interface IndexedSmoosher<T> {
    /**
     * Determine whether or not this object can smoosh content starting with the given line.
     * 
     * @param line
     *            The line at the beginning of the collection of text to be smooshed.
     * @return {@code true} if the content begun by this line can be smooshed; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    boolean canSmoosh(String line);

    /**
     * Smoosh a list together.
     * 
     * @param startingIndex
     *            The index of the given list at which to start.
     * @param list
     *            A {@link List} of {@link String} objects that are to be smooshed.
     * @return A {@code T} product of the smooshing.
     * @throws IllegalArgumentException
     *             If:
     *             <ul>
     *             <li>The starting index is negative</li>
     *             <li>The given list is {@code null}</li>
     *             <li>The list is empty</li>
     *             <li>The starting index is too high for the given list</li>
     *             </ul>
     */
    T smoosh(int startingIndex, List<String> list);

    /**
     * Get the last index examined during {@link #smoosh(int, List) smooshing}.
     * 
     * @return The last-examined index.
     * @throws IllegalStateException
     *             If {@link #smoosh(int, List)} has not been previously successfully called on this object.
     */
    int getEndingIndex();
}
