package com.cerner.ccl.parser.text.smoosh;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.text.data.util.DocumentationParserSupport;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Skeleton definition of a documentation block smoosher: starting from the given line, it will "smoosh" together all
 * text that it finds until it either encounters an {@link #isTagOpen(String) opening tag} or a
 * {@link #isCommentClose(String) comment block closure}.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractDocumentationBlockIndexedSmoosher extends DocumentationParserSupport
        implements IndexedSmoosher<String> {
    private int endingIndex = -1;

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

        final EtmPoint point = PointFactory.getPoint(AbstractDocumentationBlockIndexedSmoosher.class,
                "smoosh(int, List)");
        try {
            endingIndex = startingIndex;
            final String startingLine = normalize(list.get(startingIndex));
            if (isCommentClose(startingLine)) {
                return normalize(stripCommentClose(startingLine));
            }

            final StringBuilder builder = new StringBuilder(startingLine);
            for (int i = startingIndex + 1, size = list.size(); i < size; i++) {
                endingIndex = i;
                final String currentLine = normalize(list.get(i));
                if (currentLine.startsWith("<pre>")) {
                    final String lineSeparator = System.getProperty("line.separator");
                    builder.append(currentLine).append(lineSeparator);
                    int preIterator = i + 1;
                    for (; preIterator < size && !list.get(preIterator).contains("</pre>"); preIterator++) {
                        final String preLine = list.get(preIterator);
                        final boolean startsWithStar = StringUtils.strip(preLine).startsWith("*");
                        builder.append(startsWithStar ? preLine.substring(preLine.indexOf('*') + 1) : preLine)
                                .append(lineSeparator);
                    }
                    builder.append("</pre>");
                    i = preIterator;
                    continue;
                }
                if (isTagOpen(currentLine)) {
                    break;
                }

                if (isCommentClose(currentLine)) {
                    builder.append(" ").append(stripCommentClose(currentLine));
                    break;
                }
                builder.append(" ").append(currentLine);
            }

            return builder.toString().trim();
        } finally {
            point.collect();
        }
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
}
