package com.cerner.ccl.parser.text.documentation.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.ScriptArgument;
import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.text.smoosh.DocumentationBlockIndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser that produces a {@link ScriptDocumentation} object out of a script-level documentation block.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptDocumentationParser extends AbstractIndexedParser<ScriptDocumentation> {
    private final ScriptArgumentParser argumentParser = new ScriptArgumentParser();

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
    public ScriptDocumentation parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        try {
            final SingleTagParser boundTransactionParser = new SingleTagParser("@boundTransaction");

            final List<String> commentBlock = getCommentBlock(startingIndex, lines);
            final List<ScriptArgument> arguments = argumentParser.parse(commentBlock);
            final DocumentationBlockIndexedSmoosher descriptionSmoosher = new DocumentationBlockIndexedSmoosher();

            String description = null;
            Integer boundTransaction = null;

            // Start at 1, as 0 is assumed to be "/**"
            int iterator = 1;
            for (final int size = commentBlock.size(); iterator < size; iterator++) {
                final String currentLine = StringUtils.strip(commentBlock.get(iterator));
                if (isCommentClose(currentLine)) {
                    break;
                }

                if (description == null && descriptionSmoosher.canSmoosh(currentLine)) {
                    description = descriptionSmoosher.smoosh(iterator, commentBlock);

                    final int endingIndex = descriptionSmoosher.getEndingIndex();
                    if (endingIndex > iterator) {
                        iterator = endingIndex - 1;
                    }
                } else if (boundTransaction == null && boundTransactionParser.canParse(currentLine)) {
                    boundTransaction = Integer
                            .valueOf(Integer.parseInt(boundTransactionParser.parse(iterator, commentBlock)));
                    break;
                }
            }
            setEndingIndex(iterator);

            return new ScriptDocumentation(description, boundTransaction, arguments);
        } finally {
            point.collect();
        }
    }

    /**
     * Pull the comment block out of the entirety of the given source.
     *
     * @param startingIndex
     *            The index within the given source at which to start collecting the documentation block.
     * @param lines
     *            A {@link List} of {@link String} objects representing the lines of source from which the comment block
     *            is to be drawn.
     * @return A {@link List} of {@link String} objects representing the comment block.
     */
    private List<String> getCommentBlock(final int startingIndex, final List<String> lines) {
        final List<String> commentLines = new ArrayList<String>();
        for (int i = startingIndex, size = lines.size(); i < size; i++) {
            final String currentLine = lines.get(i);
            commentLines.add(currentLine);
            if (isCommentClose(currentLine)) {
                break;
            }
        }

        return commentLines;
    }
}
