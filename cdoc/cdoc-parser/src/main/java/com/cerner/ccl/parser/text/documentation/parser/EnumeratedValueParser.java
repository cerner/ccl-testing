package com.cerner.ccl.parser.text.documentation.parser;

import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.exception.InvalidDocumentationException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser that produces {@link EnumeratedValue} objects out of the given line.
 *
 * @author Joshua Hyde
 *
 */

public class EnumeratedValueParser extends AbstractMultiTagParser<EnumeratedValue> {

    @Override
    protected boolean isParseable(final String line) {
        return line.startsWith("@value");
    }

    @Override
    protected EnumeratedValue parseElement(final String line) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parseElement(String)");
        try {
            final int firstSpace = line.indexOf(' ');
            if (firstSpace < 0) {
                throw new InvalidDocumentationException("No space found in value definition: " + line);
            }

            final int secondSpace = line.indexOf(' ', firstSpace + 1);
            // No documentation, just the value
            if (secondSpace < 0) {
                return new EnumeratedValue(decodeFieldValue(line.substring(firstSpace + 1)));
            }
            // If it's a string value, then find the location of the very last quotation mark
            if (line.charAt(firstSpace + 1) == '"') {
                return getStringValue(firstSpace + 1, line);
            }

            final String fieldValue = decodeFieldValue(line.substring(firstSpace + 1, secondSpace));
            final String description = line.substring(secondSpace + 1);
            return new EnumeratedValue(fieldValue, description);
        } finally {
            point.collect();
        }
    }

    /**
     * Remove any encoding or escaping of data from the given value.
     *
     * @param value
     *            The line to be decoded.
     * @return The given value, decoded.
     */
    private String decodeFieldValue(final String value) {
        return value.replaceAll("\"\"", "\"");
    }

    /**
     * If the actual enumerated value is a string value in quotes, parse out the value.
     *
     * @param startingPos
     *            The position within the given line at which parsing should begin.
     * @param line
     *            The string out of which the value is to be parsed.
     * @return An {@link EnumeratedValue} representing the parsed value.
     * @throws InvalidDocumentationException
     *             If the string enumerated value does not have a closing quotation mark.
     */
    private EnumeratedValue getStringValue(final int startingPos, final String line) {
        int endingPos = -1;
        for (int i = startingPos + 1, size = line.length(); i < size; i++) {
            if (line.charAt(i) == '"') {
                /*
                 * Make sure that this isn't escaping another quotation. If it's at the end of the line, then it's got
                 * to be the closing quotation or the next character is not a " character, then this really is the close
                 * of the value.
                 */
                if (i == size - 1) {
                    endingPos = i;
                    break;
                }

                /*
                 * If the next character is a quote, skip its consideration - this current character is merely escaping
                 * it
                 */
                if (line.charAt(i + 1) == '"') {
                    i++;
                    continue;
                }

                endingPos = i;
                break;
            }
        }

        if (endingPos < 0) {
            throw new InvalidDocumentationException("Unable to find closing quotation of field value: " + line);
        }

        final String value = decodeFieldValue(line.substring(startingPos + 1, endingPos));
        return endingPos + 2 >= line.length() ? new EnumeratedValue(value)
                : new EnumeratedValue(value, line.substring(endingPos + 2));
    }

}
