package com.cerner.ccl.parser.text.documentation.parser;

import java.util.LinkedList;
import java.util.List;

import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Skeleton definition of a parser that can parse a segment of a comment block for multiple instances of a documentation
 * tag, such as {@code @codeSet} or {@code @value}.
 *
 * @author Joshua Hyde
 *
 * @param <T>
 *            The type of object to be produced by this parser.
 */

public abstract class AbstractMultiTagParser<T> {
    /**
     * Parse documentation and construct documentation objects out of it.
     *
     * @param documentation
     *            A {@link List} of {@link String} objects representing the documentation to be parsed.
     * @return A {@link List} of {@code T} objects representing the parsed documentation objects.
     * @throws IllegalArgumentException
     *             If the given list is {@code null}.
     */
    public List<T> parse(final List<String> documentation) {
        if (documentation == null) {
            throw new IllegalArgumentException("Documentation list cannot be null.");
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(List)");
        try {
            final List<T> parsed = new LinkedList<T>();
            for (int i = 0, size = documentation.size(); i < size; i++) {
                final String currentLine = documentation.get(i);
                if (isParseable(currentLine)) {
                    final DocumentationTagIndexedSmoosher indexedSmoosher = new DocumentationTagIndexedSmoosher();
                    parsed.add(parseElement(indexedSmoosher.smoosh(i, documentation)));
                    // Only modify the loop if the next line was actually proceeded to
                    final int endingIndex = indexedSmoosher.getEndingIndex();
                    if (endingIndex > i) {
                        i = endingIndex - 1; // -1 to account for i++
                    }
                }
            }

            return parsed;
        } finally {
            point.collect();
        }
    }

    /**
     * Determine whether or not the given line can be parsed.
     *
     * @param line
     *            The line whose parseability is to be determined.
     * @return {@code true} if the line can be parsed; {@code false} if not.
     */
    protected abstract boolean isParseable(String line);

    /**
     * Parse the given line and create the documentation object.
     *
     * @param line
     *            The line to be parsed.
     * @return An instance of {@code T}.
     */
    protected abstract T parseElement(String line);
}
