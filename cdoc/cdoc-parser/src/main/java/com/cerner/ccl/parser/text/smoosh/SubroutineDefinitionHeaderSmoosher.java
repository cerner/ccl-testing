package com.cerner.ccl.parser.text.smoosh;

import java.util.List;
import java.util.Locale;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A smoosher used to <b>only</b> aggregate the <i>header</i> of a CCL subroutine definition, e.g.:
 *
 * <pre>
 *  subroutine some_sub(arg1, arg2)
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDefinitionHeaderSmoosher implements IndexedSmoosher<String> {
    private int endingIndex = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSmoosh(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return line.toUpperCase(Locale.US).trim().startsWith("SUBROUTINE ");
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

            final String firstLine = list.get(startingIndex).trim();
            if (isDefinitionClose(firstLine)) {
                return firstLine;
            }

            final StringBuilder definitionBuilder = new StringBuilder(firstLine);
            boolean hasClose = false;
            int iterator = startingIndex + 1;
            for (final int size = list.size(); iterator < size; iterator++) {
                endingIndex = iterator;
                final String currentLine = list.get(iterator);
                hasClose = isDefinitionClose(currentLine);

                definitionBuilder.append(" ").append(currentLine.trim());

                if (hasClose) {
                    break;
                }
            }

            if (!hasClose) {
                throw new InvalidSubroutineException(
                        "Unable to find close to subroutine definition: " + definitionBuilder.toString());
            }

            return definitionBuilder.toString();
        } finally {
            point.collect();
        }
    }

    /**
     * Determine whether or not the given line contains the close of the subroutine definition.
     *
     * @param line
     *            The line to be examined.
     * @return {@code true} if the given line contains the close of the subroutine definition; {@code false} if not.
     */
    private boolean isDefinitionClose(final String line) {
        return line.contains(")");
    }

}
