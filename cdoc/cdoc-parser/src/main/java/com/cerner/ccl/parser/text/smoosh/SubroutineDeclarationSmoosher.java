package com.cerner.ccl.parser.text.smoosh;

import java.util.List;
import java.util.Locale;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A class to smoosh together a multi-line subroutine declaration into a single line of text.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDeclarationSmoosher implements IndexedSmoosher<String> {
    private int endingIndex = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSmoosh(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        final String normalized = line.trim().toUpperCase(Locale.US);
        if (!normalized.startsWith("DECLARE ")) {
            return false;
        }

        /*
         * In a variable declaration, there will be no parenthesis preceding an equals sign
         */
        final int parenPos = line.indexOf('(');
        if (parenPos < 0) {
            return false;
        }

        if (line.indexOf('=') < parenPos) {
            return false;
        }

        return true;
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

            final String firstLine = uncomment(list.get(startingIndex));
            if (isSubroutineDeclarationClose(firstLine)) {
                return firstLine;
            }

            final StringBuilder declarationBuilder = new StringBuilder(firstLine);
            boolean hasClose = false;
            int iterator = startingIndex + 1;
            for (final int size = list.size(); iterator < size; iterator++) {
                endingIndex = iterator;

                final String currentLine = uncomment(list.get(iterator));
                hasClose = isSubroutineDeclarationClose(currentLine);

                declarationBuilder.append(" ").append(currentLine.trim());

                if (hasClose) {
                    break;
                }
            }

            if (!hasClose) {
                throw new InvalidSubroutineException(
                        "Unable to find close to subroutine declaration: " + declarationBuilder.toString());
            }

            return declarationBuilder.toString();
        } finally {
            point.collect();
        }
    }

    /**
     * Determine whether or not the given line contains the close of a subroutine declaration.
     *
     * @param line
     *            The line to be examined for the closing of a subroutine.
     * @return {@code true} if the given line contains the close of a subroutine declaration; {@code false} if not.
     */
    private boolean isSubroutineDeclarationClose(final String line) {
        /*
         * The close to a subroutine declaration will look something like "declare sub(null) = null with protect"; thus,
         * with all whitespace and tabs stripped out, the close - and only the close - of a declaration will contain
         * ")=" from the portion declaring the return type.
         */
        return line.replaceAll(" ", "").contains(")=");
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
