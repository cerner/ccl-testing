package com.cerner.ccl.parser.text.subroutine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An object that parses the definition of a subroutine.
 *
 * @author Joshua Hyde
 * @see SubroutineDefinition
 */

public class SubroutineDefinitionParser {
    /**
     * Parse a subroutine definition.
     *
     * @param subroutineDefinition
     *            The subroutine definition to be parsed; e.g.:
     *
     *            <pre>
     *   subroutine some_subroutine(an_argument, another_argument)
     *            </pre>
     *
     * @return A {@link SubroutineDefinition} representing the given definition.
     * @throws IllegalArgumentException
     *             If the given subroutine definition is {@code null}.
     * @throws InvalidSubroutineException
     *             If the given definition is syntactically incorrect.
     */
    public SubroutineDefinition parse(final String subroutineDefinition) {
        if (subroutineDefinition == null) {
            throw new IllegalArgumentException("Subroutine definition cannot be null.");
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(String)");
        try {
            return new SubroutineDefinition(parseSubroutineName(subroutineDefinition),
                    parseArgumentNames(subroutineDefinition));
        } finally {
            point.collect();
        }
    }

    /**
     * Parse the argument names from the subroutine definition.
     *
     * @param subroutineDefinition
     *            The subroutine definition from which the definition is to be parsed.
     * @return A {@link List} of {@link String} objects representing the subroutine definition.
     * @throws InvalidSubroutineException
     *             If the arguments definition is invalid.
     */
    private List<String> parseArgumentNames(final String subroutineDefinition) {
        final int openParenPos = subroutineDefinition.indexOf('(');
        if (openParenPos < 0) {
            throw new InvalidSubroutineException(
                    "No opening parenthesis found in definition; cannot retrieve argument names: "
                            + subroutineDefinition);
        }

        final int closeParenPos = subroutineDefinition.indexOf(')', openParenPos);
        if (closeParenPos < 0) {
            throw new InvalidSubroutineException(
                    "Unable to find closing parenthesis after open parenthesis: " + subroutineDefinition);
        }

        final String argumentsDefinition = subroutineDefinition.substring(openParenPos + 1, closeParenPos).trim();
        if ("NULL".equalsIgnoreCase(argumentsDefinition)) {
            return Collections.emptyList();
        }

        final String[] splitArgs = argumentsDefinition.split(",");
        final List<String> arguments = new ArrayList<String>(splitArgs.length);
        for (final String argument : splitArgs) {
            arguments.add(argument.split("=")[0].trim());
        }

        return arguments;
    }

    /**
     * Parse the subroutine name from the subroutine definition.
     *
     * @param subroutineDefinition
     *            The subroutine definition from which the definition is to be parsed.
     * @return The name of the subroutine.
     * @throws InvalidSubroutineException
     *             If the subroutine name cannot be parsed from the definition.
     */
    private String parseSubroutineName(final String subroutineDefinition) {
        final int subroutinePos = subroutineDefinition.toUpperCase(Locale.US).indexOf("SUBROUTINE ");
        if (subroutinePos < 0) {
            throw new InvalidSubroutineException(
                    "Unable to retrieve subroutine name from definition: " + subroutineDefinition);
        }

        final int parenPos = subroutineDefinition.indexOf('(');
        if (parenPos < 0) {
            throw new InvalidSubroutineException(
                    "Unable to find opening parenthesis in definition: " + subroutineDefinition);
        }

        if (parenPos < subroutinePos) {
            throw new InvalidSubroutineException(
                    "Opening parenthesis precedes the subroutine keyword: " + subroutineDefinition);
        }

        return subroutineDefinition.substring(subroutinePos + "SUBROUTINE ".length(), parenPos).trim();
    }
}
