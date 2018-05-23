package com.cerner.ccl.parser.text.documentation.parser;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.exception.InvalidDocumentationException;
import com.cerner.ccl.parser.text.documentation.Field;
import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser for parsing a {@code @field} documentation tag into {@link Field} objects.
 *
 * @author Joshua Hyde
 *
 */

public class FieldParser extends AbstractIndexedParser<Field> {
    private final EnumeratedValueParser valueParser = new EnumeratedValueParser();
    private final CodeSetParser codeSetParser = new CodeSetParser();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final String line) {
        return normalize(line).startsWith("@field");
    }

    @Override
    public Field parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        try {
            final List<String> fieldDocumentation = new LinkedList<String>();
            fieldDocumentation.add(normalize(lines.get(startingIndex)));

            int iterator = startingIndex + 1;
            for (final int size = lines.size(); iterator < size; iterator++) {
                final String nextLine = lines.get(iterator);
                final boolean isCommentClose = isCommentClose(nextLine);
                final String currentLine = normalize(isCommentClose ? stripCommentClose(nextLine) : nextLine);

                if (isTerminatingTagOpen(currentLine)) {
                    break;
                }

                if (!StringUtils.isEmpty(currentLine)) {
                    fieldDocumentation.add(currentLine);
                }

                if (isCommentClose) {
                    break;
                }
            }
            setEndingIndex(iterator);

            final String name = getName(fieldDocumentation);
            final String description = getDescription(name, fieldDocumentation);
            final List<CodeSet> codeSets = codeSetParser.parse(fieldDocumentation);
            final List<EnumeratedValue> values = valueParser.parse(fieldDocumentation);
            final boolean optional = isOptional(fieldDocumentation);
            return new Field(name, description, optional, values, codeSets);
        } finally {
            point.collect();
        }
    }

    /**
     * Parse the description of the field.
     *
     * @param fieldName
     *            The name of the field.
     * @param documentation
     *            A {@link List} of {@link String} objects representing the documentation.
     * @return The description of the field, if any.
     */
    private String getDescription(final String fieldName, final List<String> documentation) {
        final String declaration = getFieldDeclaration(documentation);
        final int namePos = declaration.indexOf(fieldName + " ");
        // If there's nothing after the name, then there's no documentation
        if (namePos < 0) {
            return "";
        }
        return declaration.substring(namePos + (fieldName + " ").length());
    }

    /**
     * Smoosh together the field declaration from the given documentation block.
     *
     * @param documentation
     *            A {@link List} of {@link String} objects representing the documentation block.
     * @return The field declaration.
     * @throws InvalidDocumentationException
     *             If the field declaration cannot be found in the given documentation block.
     */
    private String getFieldDeclaration(final List<String> documentation) {
        int currentIndex = 0;
        for (final String line : documentation) {
            if (line.startsWith("@field")) {
                return new DocumentationTagIndexedSmoosher().smoosh(currentIndex, documentation);
            }
            currentIndex++;
        }

        throw new InvalidDocumentationException(
                "Unable to parse field declaration from documentation: " + documentation);
    }

    /**
     * Get the name of the field.
     *
     * @param documentation
     *            A {@link List} of {@link String} objects representing the documentation block from which the name is
     *            to be parsed.
     * @return The name of the field.
     * @throws InvalidDocumentationException
     *             If no name can be parsed from the documentation.
     */
    private String getName(final List<String> documentation) {
        final String declaration = getFieldDeclaration(documentation);
        final int spacePos = declaration.indexOf(' ');
        if (spacePos < 0) {
            throw new InvalidDocumentationException("No name can be parsed from field documentation: " + declaration);
        }

        final int closingSpacePos = declaration.indexOf(' ', spacePos + 1);
        return closingSpacePos < 0 ? declaration.substring(spacePos + 1)
                : declaration.substring(spacePos + 1, closingSpacePos);
    }

    /**
     * Determine whether or not the field has been marked optional.
     *
     * @param documentation
     *            A {@link List} of {@link String} objects representing the documentation block from which optionality
     *            is to be determined.
     * @return {@code true} if the field has been marked optional; {@code false} if not.
     */
    private boolean isOptional(final List<String> documentation) {
        for (final String line : documentation) {
            if (line.startsWith("@optional")) {
                return true;
            }
        }

        return false;
    }

}
