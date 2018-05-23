package com.cerner.ccl.parser.text.documentation.parser;

import java.util.List;

import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser that parses out the documentation for a single tag.
 *
 * @author Joshua Hyde
 *
 */

public class SingleTagParser extends AbstractIndexedParser<String> {
    private final String tag;

    /**
     * Create a parser.
     *
     * @param tag
     *            The tag to be looked for for parsing.
     * @throws IllegalArgumentException
     *             If the given tag is {@code null}.
     */
    public SingleTagParser(final String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }

        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }
        return line.startsWith(tag);
    }

    @Override
    public String parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        try {
            final DocumentationTagIndexedSmoosher smoosher = new DocumentationTagIndexedSmoosher();
            final String smooshed = smoosher.smoosh(startingIndex, lines);
            setEndingIndex(smoosher.getEndingIndex());

            final int spacePos = smooshed.indexOf(' ');
            if (spacePos < 0) {
                return "";
            }

            return smooshed.substring(spacePos + 1);
        } finally {
            point.collect();
        }
    }
}
