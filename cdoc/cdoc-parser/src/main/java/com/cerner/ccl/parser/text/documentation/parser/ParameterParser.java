package com.cerner.ccl.parser.text.documentation.parser;

import java.util.List;

import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser to parse {@link Parameter} objects from documentation.
 *
 * @author Joshua Hyde
 *
 */

public class ParameterParser extends AbstractIndexedParser<Parameter> {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final String line) {
        return line.startsWith("@param");
    }

    @Override
    public Parameter parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        try {
            final DocumentationTagIndexedSmoosher smoosher = new DocumentationTagIndexedSmoosher();
            final String smooshed = smoosher.smoosh(startingIndex, lines);
            setEndingIndex(smoosher.getEndingIndex());

            final int tagEndPos = smooshed.indexOf(' ');
            final int descriptionStartPos = smooshed.indexOf(' ', tagEndPos + 1);
            if (descriptionStartPos < 0) {
                return new Parameter(smooshed.substring(tagEndPos + 1));
            }

            return new Parameter(smooshed.substring(tagEndPos + 1, descriptionStartPos),
                    smooshed.substring(descriptionStartPos + 1));
        } finally {
            point.collect();
        }
    }

}
