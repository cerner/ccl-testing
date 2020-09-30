package com.cerner.ccl.parser.text.subroutine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.SimpleCharacterDataTyped;
import com.cerner.ccl.parser.data.SimpleDataTyped;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;
import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A utility to parse the declaration of subroutines from source code.
 *
 * @author Joshua Hyde
 * @see SubroutineDeclaration
 */

public class SubroutineDeclarationParser {
    /**
     * Parse a subroutine declaration.
     *
     * @param declaration
     *            The subroutine declaration from which the data is to be parsed.
     * @return A {@link SubroutineDeclaration} object representing the declaration of the subroutine.
     * @throws IllegalArgumentException
     *             If the given declaration is {@code null}.
     * @throws InvalidSubroutineException
     *             If any part of the subroutine declaration is invalid.
     */
    public SubroutineDeclaration parse(final String declaration) {
        if (declaration == null) {
            throw new IllegalArgumentException("Subroutine declaration cannot be null.");
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(String)");
        String reducedDeclaration = reduceDeclaration(uncommentDeclaration(declaration).trim());
        try {
            return new SubroutineDeclaration(parseName(declaration, reducedDeclaration),
                    parseReturnType(declaration, reducedDeclaration), parseArguments(reducedDeclaration));
        } finally {
            point.collect();
        }
    }

    /**
     * Parse an argument from its definition.
     *
     * @param argumentDefinition
     *            The argument definition that is to be parsed.
     * @return A {@link SubroutineArgument} representing the argument definition.
     * @throws InvalidSubroutineException
     *             If the given argument definition is invalidly defined.
     */
    private SubroutineArgumentDeclaration parseArgument(final String argumentDefinition) {
        final int equalsPos = argumentDefinition.indexOf('=');
        if (equalsPos < 0) {
            return new SubroutineArgumentDeclaration(argumentDefinition.trim(), false, DataType.UNKNOWN);
        }

        final String argumentName = argumentDefinition.substring(0, equalsPos).trim();
        final List<String> argumentQualifiers = parseArgumentQualifiers(argumentDefinition);
        final boolean isByRef = !argumentQualifiers.isEmpty()
                && argumentQualifiers.get(0).toUpperCase(Locale.US).equals("REF");
        final String dataTypeDefinition = argumentDefinition.substring(equalsPos + 1, argumentDefinition.length())
                .trim();
        final DataType dataType = DataType.forDeclaration(dataTypeDefinition.indexOf('(') < 0 ? dataTypeDefinition
                : dataTypeDefinition.substring(0, dataTypeDefinition.indexOf('(')).trim());

        if (DataType.CHAR.equals(dataType)) {
            final int dataLength = Integer.parseInt(dataTypeDefinition.substring(1));
            return new SubroutineCharacterArgumentDeclaration(argumentName, dataLength, isByRef);
        }
        return new SubroutineArgumentDeclaration(argumentName, isByRef, dataType);
    }

    /**
     * Parse the arguments from a reduced subroutine declaration.
     *
     * @param declaration
     *            The reduced subroutine declaration from which the arguments are to be parsed.
     * @return A {@link Set} of {@link SubroutineArgument} objects representing the subroutine arguments.
     */
    private List<SubroutineArgumentDeclaration> parseArguments(final String declaration) {
        final String argsString = declaration.substring(declaration.indexOf('(') + 1, declaration.lastIndexOf(')'))
                .trim().replaceAll("(\\([^\\)]*),([^\\)]*\\))", "$1|$2");
        if ("NULL".equalsIgnoreCase(argsString) || argsString.trim().isEmpty()) {
            return Collections.<SubroutineArgumentDeclaration> emptyList();
        }

        final List<SubroutineArgumentDeclaration> arguments = new ArrayList<SubroutineArgumentDeclaration>();
        final String[] args = argsString.split(",", -1);
        for (int i = 0; i < args.length; i++) {
            arguments.add(parseArgument(args[i].replaceAll("\\|", ",")));
        }
        return arguments;
    }

    /**
     * Parse argument declaration qualifiers (e.g., {@code (REF,CURREF)}.
     *
     * @param argumentDeclaration
     *            The argument declaration from which the qualifiers are to be parsed.
     * @return A case-insensitive {@link Set} of {@link String} objects representing the declaration qualifiers for the
     *         given argument.
     */
    private List<String> parseArgumentQualifiers(final String argumentDeclaration) {
        final int openParenPos = argumentDeclaration.indexOf('(');
        if (openParenPos < 0) {
            return Collections.<String> emptyList();
        }
        final List<String> qualifiers = new ArrayList<String>();
        final int closeParenPos = argumentDeclaration.indexOf(')');
        for (final String qualifier : argumentDeclaration.substring(openParenPos + 1, closeParenPos).split(",")) {
            qualifiers.add(qualifier.trim());
        }

        return qualifiers;
    }

    /**
     * Parse the name from a reduced subroutine declaration.
     *
     * @param declaration
     *            The full subroutine declaration from which the name is to be parsed.
     * @param reducedDeclaration
     *            The reduced subroutine declaration.
     * @return The name of the subroutine.
     * @throws InvalidSubroutineException
     *             If the given declaration is a malformed declaration.
     */
    private String parseName(final String declaration, final String reducedDeclaration) {
        final int parenPos = reducedDeclaration.indexOf('(');
        if (parenPos < 0) {
            throw new InvalidSubroutineException(
                    "No opening parenthesis found in subroutine declaration: " + declaration);
        }
        return reducedDeclaration.substring(0, parenPos).trim();
    }

    /**
     * Parse the return type of a reduced subroutine declaration.
     *
     * @param declaration
     *            The subroutine declaration from which the data type is to be parsed.
     * @param declaration
     *            The reduced subroutine declaration.
     * @return {@code null} if the subroutine is a {@code void} subroutine; otherwise, a {@link DataType} enum
     *         representing the return type of the subroutine.
     * @throws InvalidSubroutineException
     *             If the subroutine declaration does not contain a return type declaration.
     */
    private DataTyped parseReturnType(final String declaration, final String reducedDeclaration) {
        final int finalRightParenPos = reducedDeclaration.lastIndexOf(')');
        final int returnTypeEqualsPos = reducedDeclaration.indexOf('=', finalRightParenPos);
        if (returnTypeEqualsPos < 0) {
            throw new InvalidSubroutineException("No return data type found within declaration: " + declaration);
        }
        final String normalized = reducedDeclaration.toUpperCase(Locale.US);
        final int withPos = normalized.indexOf("WITH", returnTypeEqualsPos + 1);
        final String returnTypeDefinition = (withPos < 0 ? reducedDeclaration.substring(returnTypeEqualsPos + 1)
                : reducedDeclaration.substring(returnTypeEqualsPos + 1, withPos)).trim();
        if (returnTypeDefinition.equalsIgnoreCase("NULL")) {
            return null;
        }

        final DataType dataType = DataType.forDeclaration(returnTypeDefinition);
        if (DataType.CHAR.equals(dataType)) {
            final int dataLength = Integer.parseInt(returnTypeDefinition.substring(1));
            return new SimpleCharacterDataTyped(dataLength);
        }
        return new SimpleDataTyped(dataType);
    }

    /**
     * Reduces a subroutine declaration string to just the subroutine header string. For example both of
     *
     * <pre>
     * declare some_sub(arg1 = type1) = some_type with some_option
     * </pre>
     *
     * and
     *
     * <pre>
     * subroutine (some_sub(arg1 = type1) = some_type with some_option)
     * </pre>
     *
     * will be reduced to
     *
     * <pre>
     * some_sub(arg1 = type1) = some_type with some_option
     * </pre>
     *
     * @param declaration
     * @return The subroutine header.
     */
    private String reduceDeclaration(final String declaration) {
        Pattern inlinePattern = Pattern.compile("subroutine\\s*\\x28(.*)", Pattern.CASE_INSENSITIVE);
        Matcher inlineMatcher = inlinePattern.matcher(declaration);
        if (inlineMatcher.matches()) {
            String capture = inlineMatcher.group(1).trim();
            return capture.substring(0, capture.length() - 1).trim();
        }
        Pattern declarePattern = Pattern.compile("declare (.*)", Pattern.CASE_INSENSITIVE);
        Matcher declareMatcher = declarePattern.matcher(declaration);
        if (declareMatcher.matches()) {
            return declareMatcher.group(1).trim();
        }
        throw new InvalidSubroutineException(
                "SubroutineDeclarationParser.parse inovked with an invalid subroutine declaration: " + declaration);
    }

    /**
     * Removes in-line and trailing comments from a subroutine declaration
     *
     * @param declaration
     *            The subroutine declaration.
     * @return The uncommented declaration.
     */
    private String uncommentDeclaration(final String declaration) {
        return declaration.replaceAll("\\/\\*.*?\\*\\/", "").replaceAll(";.*", "");

    }
}
