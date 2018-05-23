package com.cerner.ccl.parser.text.documentation.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.text.documentation.AbstractDocumentation;
import com.cerner.ccl.parser.text.documentation.Field;
import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.documentation.RecordStructureDocumentation;
import com.cerner.ccl.parser.text.documentation.SubroutineDocumentation;
import com.cerner.ccl.parser.text.smoosh.DocumentationBlockIndexedSmoosher;
import com.cerner.ccl.parser.text.smoosh.IndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser of documentation into a {@link AbstractDocumentation} object.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentationParser extends AbstractIndexedParser<AbstractDocumentation> {
    private final IndexedSmoosher<String> descriptionSmoosher = new DocumentationBlockIndexedSmoosher();
    private final ParameterParser parameterParser = new ParameterParser();
    private final FieldParser fieldParser = new FieldParser();
    private final SingleTagParser returnParser = new SingleTagParser("@returns");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return isCommentStart(line);
    }

    @Override
    public AbstractDocumentation parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        try {
            final List<Parameter> parameters = new ArrayList<Parameter>();
            final List<Field> fields = new ArrayList<Field>();
            String description = null;
            String returnDescription = null;
            InterfaceStructureType type = null;

            // Ignore the actual starting index, as that's likely to be "/**"
            int iterator = startingIndex + 1;
            for (final int size = lines.size(); iterator < size; iterator++) {
                final String currentLine = normalize(lines.get(iterator));
                if (isCommentClose(currentLine)) {
                    break;
                }

                if (StringUtils.isBlank(currentLine)) {
                    continue;
                }

                if (parameterParser.canParse(currentLine)) {
                    parameters.add(parameterParser.parse(iterator, lines));
                    iterator = determineNextIndex(iterator, parameterParser.getEndingIndex());
                } else if (fieldParser.canParse(currentLine)) {
                    fields.add(fieldParser.parse(iterator, lines));
                    iterator = determineNextIndex(iterator, fieldParser.getEndingIndex());
                } else if (returnParser.canParse(currentLine)) {
                    returnDescription = returnParser.parse(iterator, lines);
                    iterator = determineNextIndex(iterator, returnParser.getEndingIndex());
                } else if (type == null && (currentLine.contains("@request") || currentLine.contains("@reply"))) {
                    if (currentLine.contains("@request")) {
                        type = InterfaceStructureType.REQUEST;
                    } else if (currentLine.contains("@reply")) {
                        type = InterfaceStructureType.REPLY;
                    }
                } else if (description == null && descriptionSmoosher.canSmoosh(currentLine)) {
                    description = descriptionSmoosher.smoosh(iterator, lines);
                    iterator = determineNextIndex(iterator, descriptionSmoosher.getEndingIndex());
                }
            }

            return fields.isEmpty() ? new SubroutineDocumentation(description, parameters, returnDescription)
                    : new RecordStructureDocumentation(description, type, fields);
        } finally {
            point.collect();
        }
    }

    /**
     * Determine the next index from which to resume scanning the source code.
     *
     * @param currentIndex
     *            The index at which the current round of examination began.
     * @param parserEndingIndex
     *            The index of the line which the parser in question last examined.
     * @return The index at which to resume scanning, less one to account for incrementation within an iteration.
     */
    private int determineNextIndex(final int currentIndex, final int parserEndingIndex) {
        if (parserEndingIndex > currentIndex) {
            return parserEndingIndex - 1;
        }
        return currentIndex;
    }
}
