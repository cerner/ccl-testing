package com.cerner.ccl.parser.text.smoosh;

import java.util.List;
import java.util.regex.Pattern;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A smoosher used to <b>only</b> aggregate the <i>header</i> of an in-line CCL subroutine definition, e.g.:
 *
 * <pre>
 * subroutine(some_sub(arg1 = type1, arg2 = type2) = return_type)
 * </pre>
 *
 * @author Fred Eckertson
 *
 */

public class InlineSubroutineDeclarationSmoosher implements IndexedSmoosher<String> {
    private static final String PATTERN_LEFT_PAREN = "\\x28";
    private static final String PATTERN_RIGTH_PAREN = "\\x29";
    private int endingIndex = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSmoosh(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return Pattern.compile("^\\s*subroutine\\s*\\(.*", Pattern.CASE_INSENSITIVE).matcher(line).matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndingIndex() {
        if (endingIndex < 0) {
            throw new IllegalStateException("Smoosh has not been invoked on this object.");
        }

        return endingIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String smoosh(final int startingIndex, final List<String> list) {
        if (startingIndex < 0) {
            throw new IllegalArgumentException("Starting index cannot be negative.");
        }

        if (list == null) {
            throw new IllegalArgumentException("List cannot be null.");
        }

        if (list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be empty.");
        }

        if (startingIndex >= list.size()) {
            throw new IllegalArgumentException("Starting index exceeds list size; index = "
                    + Integer.toString(startingIndex) + "; size = " + Integer.toString(list.size()));
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "smoosh(int, List)");
        try {
            endingIndex = startingIndex;

            final String firstLine = uncomment(list.get(startingIndex)).trim();
            int leftCount = matchCount(firstLine, PATTERN_LEFT_PAREN);
            int rightCount = matchCount(firstLine, PATTERN_RIGTH_PAREN);

            if (leftCount == rightCount) {
                return firstLine;
            }

            final StringBuilder definitionBuilder = new StringBuilder(firstLine);
            int iterator = startingIndex + 1;
            for (final int size = list.size(); iterator < size; iterator++) {
                endingIndex = iterator;
                final String currentLine = uncomment(list.get(iterator));
                leftCount += matchCount(currentLine, PATTERN_LEFT_PAREN);
                rightCount += matchCount(currentLine, PATTERN_RIGTH_PAREN);

                definitionBuilder.append(" ").append(currentLine.trim());

                if (leftCount == rightCount) {
                    break;
                }
            }

            if (leftCount != rightCount) {
                throw new InvalidSubroutineException(
                        "Unable to find close to subroutine definition: " + definitionBuilder.toString());
            }

            return definitionBuilder.toString().trim();
        } finally {
            point.collect();
        }
    }

    /**
     * Counts the number of non-overlapping occurrences of one string within another
     *
     * @param source
     *            The string to search.
     * @param expression
     *            The expression to search for
     * @return The number of occurrences of expression within source.
     */
    private int matchCount(final String source, final String expression) {
        return source.split(expression, -1).length - 1;
    }

    /**
     * Removes in-line and trailing comments from a line of code.
     *
     * @param line
     *            The line of code.
     * @return The uncommented line.
     */
    private String uncomment(final String line) {
        return line.replaceAll("\\/\\*.*?\\*\\/", "").replaceAll(";.*", "");
    }
}
