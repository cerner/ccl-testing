package com.cerner.ccl.parser.text.documentation.parser;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.exception.InvalidDocumentationException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser to produce {@link CodeSet} objects out of {@code @codeSet} documentation tags.
 *
 * @author Joshua Hyde
 *
 */

public class CodeSetParser extends AbstractMultiTagParser<CodeSet> {

    @Override
    protected boolean isParseable(final String line) {
        return line.startsWith("@codeSet");
    }

    @Override
    protected CodeSet parseElement(final String line) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parseElement(String)");
        try {
            final int firstSpace = line.indexOf(' ');
            if (firstSpace < 0) {
                throw new InvalidDocumentationException("No space found in code set definition: " + line);
            }

            final int secondSpace = line.indexOf(' ', firstSpace + 1);
            // No documentation, just the code set number
            if (secondSpace < 0) {
                return new CodeSet(Integer.parseInt(line.substring(firstSpace + 1)));
            }
            final int codeSet = Integer.parseInt(line.substring(firstSpace + 1, secondSpace));
            final String description = line.substring(secondSpace + 1);
            return new CodeSet(codeSet, description);
        } finally {
            point.collect();
        }
    }

}
