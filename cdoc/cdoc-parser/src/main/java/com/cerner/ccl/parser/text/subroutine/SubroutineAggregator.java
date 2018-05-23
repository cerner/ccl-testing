package com.cerner.ccl.parser.text.subroutine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.subroutine.Subroutine;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;
import com.cerner.ccl.parser.data.subroutine.SubroutineCharacterArgument;
import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.documentation.SubroutineDocumentation;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A class that can aggregate declarations, definitions, and documentation into a single subroutine object.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineAggregator {
    /**
     * Aggregate the pieces of a subroutine into a single subroutine piece.
     *
     * @param declarations
     *            A {@link Map}; the keys are the names of the subroutines and their lookup is assumed to be
     *            case-insensitive, and the values are {@link SubroutineDeclaration} objects representing the
     *            declarations of the subroutines.
     * @param definitions
     *            A {@link List} of {@link SubroutineDefinition} objects representing the defined subroutines within the
     *            script.
     * @param subroutineDocumentation
     *            A {@link Map}; the keys are the names of the subroutines and their lookup is assumed to be
     *            case-insensitive, and the values are {@link SubroutineDocumentation} objects representing the
     *            documentation of each subroutine.
     * @return A {@link List} of {@link Subroutine} objects representing the aggregation of all the bits of data into a
     *         single object representing the subroutine.
     */
    public List<Subroutine> aggregate(final Map<String, SubroutineDeclaration> declarations,
            final List<SubroutineDefinition> definitions,
            final Map<String, SubroutineDocumentation> subroutineDocumentation) {
        if (declarations == null) {
            throw new IllegalArgumentException("Declarations cannot be null.");
        }

        if (definitions == null) {
            throw new IllegalArgumentException("Definitions cannot be null.");
        }

        if (subroutineDocumentation == null) {
            throw new IllegalArgumentException("Subroutine documentation cannot be null.");
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "aggregate(Map, List, Map)");
        try {
            final List<Subroutine> subroutines = new ArrayList<Subroutine>(definitions.size());
            for (final SubroutineDefinition definition : definitions) {
                final String subroutineName = definition.getName();
                final SubroutineDocumentation documentation = subroutineDocumentation.get(subroutineName);
                final List<String> argumentNames = definition.getArgumentNames();
                final List<SubroutineArgument> arguments = new ArrayList<SubroutineArgument>(argumentNames.size());
                // If typing information can be determined...
                if (declarations.containsKey(subroutineName)) {
                    final SubroutineDeclaration declaration = declarations.get(subroutineName);
                    final List<SubroutineArgumentDeclaration> argumentDeclarations = declaration.getArguments();
                    for (int argumentPosition = 0; argumentPosition < argumentNames.size()
                            && argumentPosition < argumentDeclarations.size(); argumentPosition++) {
                        arguments.add(newArgument(argumentDeclarations.get(argumentPosition), documentation));
                    }
                    subroutines
                            .add(newSubroutine(subroutineName, arguments, declaration.getReturnType(), documentation));
                } else {
                    for (final String argumentName : argumentNames) {
                        arguments.add(newArgument(argumentName, documentation));
                    }
                    subroutines.add(
                            newSubroutine(subroutineName, arguments, Subroutine.UNKNOWN_RETURN_TYPE, documentation));
                }
            }
            return subroutines;
        } finally {
            point.collect();
        }
    }

    /**
     * Get the description of the given parameter from the given list of documented parameters.
     *
     * @param parameters
     *            A {@link List} of {@link Parameter} from which the documentation is to be retrieved.
     * @param parameterName
     *            The name of the prameter.
     * @return {@code null} if the requested parameter is not documented; otherwise, the description of the given
     *         parameter.
     */
    private String getDescription(final List<Parameter> parameters, final String parameterName) {
        for (final Parameter parameter : parameters) {
            if (parameterName.equalsIgnoreCase(parameter.getName())) {
                return parameter.getDescription();
            }
        }

        return null;
    }

    /**
     * Create a new subroutine argument without any typing information.
     *
     * @param argumentName
     *            The name of the argument.
     * @param documentation
     *            A {@link SubroutineDocumentation} object representing the documentation of the subroutine to which the
     *            argument belongs, if any. This can be {@code null} to indicate that there is no available
     *            documentation.
     * @return A {@link SubroutineArgument} object.
     */
    private SubroutineArgument newArgument(final String argumentName, final SubroutineDocumentation documentation) {
        return documentation == null ? new SubroutineArgument(argumentName)
                : new SubroutineArgument(argumentName, getDescription(documentation.getParameters(), argumentName));
    }

    /**
     * Create a new subroutine argument with typing information.
     *
     * @param source
     *            The {@link SubroutineArgumentDeclaration} serving as the source of all typing information with regard
     *            to the subroutine argument to be created.
     * @param documentation
     *            A {@link SubroutineDocumentation} object representing the documentation of the subroutine to which the
     *            argument belongs, if any. This can be {@code null} to indicate that there is no available
     *            documentation.
     * @return A {@link SubroutineArgument} object.
     */
    private SubroutineArgument newArgument(final SubroutineArgumentDeclaration source,
            final SubroutineDocumentation documentation) {
        final boolean isFixedLength = source instanceof SubroutineCharacterArgumentDeclaration;
        if (documentation == null) {
            return isFixedLength
                    ? new SubroutineCharacterArgument(source.getName(),
                            ((SubroutineCharacterArgumentDeclaration) source).getDataLength(), source.isByRef(), null)
                    : new SubroutineArgument(source.getName(), source.getDataType(), source.isByRef(), null);
        }

        for (final Parameter parameter : documentation.getParameters()) {
            if (source.getName().equalsIgnoreCase(parameter.getName())) {
                return isFixedLength
                        ? new SubroutineCharacterArgument(source.getName(),
                                ((SubroutineCharacterArgumentDeclaration) source).getDataLength(), source.isByRef(),
                                parameter.getDescription())
                        : new SubroutineArgument(source.getName(), source.getDataType(), source.isByRef(),
                                parameter.getDescription());
            }
        }

        return isFixedLength
                ? new SubroutineCharacterArgument(source.getName(),
                        ((SubroutineCharacterArgumentDeclaration) source).getDataLength(), source.isByRef(), null)
                : new SubroutineArgument(source.getName(), source.getDataType(), source.isByRef(), null);
    }

    /**
     * Create a new subroutine object.
     *
     * @param subroutineName
     *            The name of the subroutine.
     * @param arguments
     *            A {@link List} of {@link SubroutineArgument} objects representing the arguments of the subroutine.
     * @param returnType
     *            A {@link DataTyped} object representing the return type of the subroutine, if any. This can be
     *            {@code null} to indicate that there is no return type.
     * @param documentation
     *            A {@link SubroutineDocumentation} object representing the documentation of the subroutine, if any. It
     *            can be {@code null} to indicate that there is no documentation.
     * @return A {@link Subroutine} object representing the aggregation of all of the given pieces.
     */
    private Subroutine newSubroutine(final String subroutineName, final List<SubroutineArgument> arguments,
            final DataTyped returnType, final SubroutineDocumentation documentation) {
        return documentation == null ? new Subroutine(subroutineName, arguments, returnType)
                : new Subroutine(subroutineName, arguments, returnType, documentation.getDescription(),
                        documentation.getReturnDescription());
    }
}
