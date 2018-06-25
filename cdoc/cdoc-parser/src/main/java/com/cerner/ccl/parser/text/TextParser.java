package com.cerner.ccl.parser.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;
import com.cerner.ccl.parser.exception.CDocParsingException;
import com.cerner.ccl.parser.text.data.util.CaseInsensitiveComparator;
import com.cerner.ccl.parser.text.documentation.AbstractDocumentation;
import com.cerner.ccl.parser.text.documentation.RecordStructureDocumentation;
import com.cerner.ccl.parser.text.documentation.SubroutineDocumentation;
import com.cerner.ccl.parser.text.documentation.parser.DocumentationParser;
import com.cerner.ccl.parser.text.record.RecordStructureAggregator;
import com.cerner.ccl.parser.text.record.Structure;
import com.cerner.ccl.parser.text.record.parser.StructureParser;
import com.cerner.ccl.parser.text.smoosh.InlineSubroutineDeclarationSmoosher;
import com.cerner.ccl.parser.text.smoosh.SubroutineDeclarationSmoosher;
import com.cerner.ccl.parser.text.smoosh.SubroutineDefinitionHeaderSmoosher;
import com.cerner.ccl.parser.text.subroutine.SubroutineAggregator;
import com.cerner.ccl.parser.text.subroutine.SubroutineDeclaration;
import com.cerner.ccl.parser.text.subroutine.SubroutineDeclarationParser;
import com.cerner.ccl.parser.text.subroutine.SubroutineDefinition;
import com.cerner.ccl.parser.text.subroutine.SubroutineDefinitionParser;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A text-driven parser of CCL documentation.
 * <p>
 * This can be considered a stateless object insofar as its reuse to parse multiple CCL scripts.
 *
 * @author Joshua Hyde
 *
 */

public class TextParser {
    private static final String PATTERN_LEFT_PAREN = "\\x28";
    private final CclScriptFactory scriptFactory = new CclScriptFactory();
    private final IncludeFileFactory includeFactory = new IncludeFileFactory();

    private final SubroutineDefinitionParser subroutineDefinitionParser = new SubroutineDefinitionParser();
    private final SubroutineDeclarationParser subroutineDeclarationParser = new SubroutineDeclarationParser();
    private final SubroutineAggregator subroutineAggregator = new SubroutineAggregator();
    private final RecordStructureAggregator recordStructureAggregator = new RecordStructureAggregator();

    private final Logger logger = LoggerFactory.getLogger(TextParser.class);

    /**
     * Parse a CCL script's documentation.
     *
     * @param scriptName
     *            The name of the script for which documentation is to be generated. This should <i>not</i> contain the
     *            file extension - e.g., if the file "test_file.prg" is being parsed, then this should be "test_file".
     * @param source
     *            A {@link List} of {@code String} objects representing the source code whose documentation is to be
     *            parsed.
     * @return A {@link CclScript} object representing the parsed documentation.
     * @throws CDocParsingException
     *             If any errors occur while parsing the given source code.
     * @throws IllegalArgumentException
     *             If the given source or script name is {@code null}.
     */
    public CclScript parseCclScript(final String scriptName, final List<String> source) {
        return parse(scriptName, source, new ScriptDocumentationFactory(), scriptFactory);
    }

    /**
     * Parse an include file for documentation.
     *
     * @param includeName
     *            The name of the include file. For purposes of clarity, this <i>should</i> contain the file extension,
     *            such as "test_file.inc".
     * @param source
     *            A {@link List} of {@code String} objects representing the source code whose documentation is to be
     *            parsed.
     * @return A {@link IncludeFile} object representing the parsed documentation.
     * @throws CDocParsingException
     *             If any errors occur while parsing the given source code.
     * @throws IllegalArgumentException
     *             If the given source or script name is {@code null}.
     */
    public IncludeFile parseIncludeFile(final String includeName, final List<String> source) {
        return parse(includeName, source, new IncludeDocumentationFactory(), includeFactory);
    }

    /**
     * Parse source into a documented object.
     *
     * @param <T>
     *            The type of documented object to be returned.
     * @param <D>
     *            The type of {@link Described} object to be created as top-level documentation.
     * @param objectName
     *            The name of the object to be parsed.
     * @param source
     *            A {@link List} of {@code String} objects representing the source code whose documentation is to be
     *            parsed.
     * @param topLevelFactory
     *            A {@link TopLevelDocumentationFactory} used to create the top-level documentation object for the
     *            documented object to be produced.
     * @param objectFactory
     *            A {@link DocumentedObjectFactory} to be used to produce the actual documented object.
     * @return A documented object constructed out of the given source.
     */
    @SuppressWarnings("unchecked")
    private <T, D extends Described> T parse(final String objectName, final List<String> source,
            final TopLevelDocumentationFactory<D> topLevelFactory, final DocumentedObjectFactory<T, D> objectFactory) {
        if (objectName == null) {
            throw new IllegalArgumentException("Object name cannot be null.");
        }

        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null.");
        }

        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(String, List, "
                + topLevelFactory.getClass().getSimpleName() + ", " + objectFactory.getClass().getSimpleName() + ")");
        try {
            // replace all tab characters with a with spaces
            for (int index = 0; index < source.size(); index++) {
                source.set(index, source.get(index).replaceAll("\\t", " ").replaceAll("  ", " "));
            }

            // declare stateful parsers and smooshers
            final SubroutineDeclarationSmoosher subroutineDeclarationSmoosher = new SubroutineDeclarationSmoosher();
            final SubroutineDefinitionHeaderSmoosher subroutineDefinitionSmoosher = new SubroutineDefinitionHeaderSmoosher();
            final InlineSubroutineDeclarationSmoosher inlineSubroutineDefinitionSmoosher = new InlineSubroutineDeclarationSmoosher();
            final DocumentationParser documentationParser = new DocumentationParser();
            final StructureParser structureParser = new StructureParser();

            final Map<String, SubroutineDeclaration> subroutineDeclarations = new TreeMap<String, SubroutineDeclaration>(
                    CaseInsensitiveComparator.getInstance());
            final Map<String, SubroutineDocumentation> subroutineDocumentation = new TreeMap<String, SubroutineDocumentation>(
                    CaseInsensitiveComparator.getInstance());
            final List<SubroutineDefinition> subroutineDefinitions = new ArrayList<SubroutineDefinition>();

            final Map<String, RecordStructureDocumentation> recordStructureDocumentation = new TreeMap<String, RecordStructureDocumentation>(
                    CaseInsensitiveComparator.getInstance());
            final List<Structure> structures = new ArrayList<Structure>();

            AbstractDocumentation lastDocumentation = null;
            Described scriptDocumentation = null;
            int currentLine = 0;
            for (final int size = source.size(); currentLine < size; currentLine++) {
                final String line = source.get(currentLine);
                if (subroutineDeclarationSmoosher.canSmoosh(line)) {
                    final String declarationText = subroutineDeclarationSmoosher.smoosh(currentLine, source);
                    final SubroutineDeclaration declaration = subroutineDeclarationParser.parse(declarationText);
                    subroutineDeclarations.put(declaration.getName(), declaration);

                    final int endingIndex = subroutineDeclarationSmoosher.getEndingIndex();
                    if (endingIndex > currentLine) {
                        currentLine = endingIndex - 1;
                    }
                } else if (inlineSubroutineDefinitionSmoosher.canSmoosh(line)) {
                    final String rawHeaderText = subroutineDeclarationSmoosher.smoosh(currentLine, source);
                    final String headerText = rawHeaderText.replaceFirst(PATTERN_LEFT_PAREN, " ").substring(0,
                            rawHeaderText.length() - 1);
                    final String declarationText = Pattern.compile("subroutine", Pattern.CASE_INSENSITIVE)
                            .matcher(rawHeaderText.substring(0, rawHeaderText.length() - 1)).replaceFirst("declare ")
                            .replaceFirst(PATTERN_LEFT_PAREN, "");
                    final SubroutineDeclaration declaration = subroutineDeclarationParser.parse(declarationText);
                    subroutineDeclarations.put(declaration.getName(), declaration);
                    final SubroutineDefinition definition = subroutineDefinitionParser.parse(headerText);
                    subroutineDefinitions.add(definition);

                    if (lastDocumentation != null) {
                        if (lastDocumentation instanceof SubroutineDocumentation) {
                            subroutineDocumentation.put(declaration.getName(),
                                    (SubroutineDocumentation) lastDocumentation);
                        } else {
                            logger.warn("CDOC warning");
                            logger.warn("The documentation immediately preceding subroutine '" + declaration.getName()
                                    + "' is not valid subroutine documentation.");
                        }
                    }
                    lastDocumentation = null;

                    final int endingIndex = subroutineDeclarationSmoosher.getEndingIndex();
                    if (endingIndex > currentLine) {
                        currentLine = endingIndex - 1;
                    }
                } else if (subroutineDefinitionSmoosher.canSmoosh(line)) {
                    final String definitionText = subroutineDefinitionSmoosher.smoosh(currentLine, source);
                    final SubroutineDefinition definition = subroutineDefinitionParser.parse(definitionText);
                    subroutineDefinitions.add(definition);

                    if (lastDocumentation != null) {
                        if (lastDocumentation instanceof SubroutineDocumentation) {
                            subroutineDocumentation.put(definition.getName(),
                                    (SubroutineDocumentation) lastDocumentation);
                        } else {
                            logger.warn("CDOC warning");
                            logger.warn("The documentation immediately preceding subroutine '" + definition.getName()
                                    + "' is not valid subroutine documentation.");
                        }
                    }
                    lastDocumentation = null;

                    final int endingIndex = subroutineDefinitionSmoosher.getEndingIndex();
                    if (endingIndex > currentLine) {
                        currentLine = endingIndex - 1;
                    }
                } else if (structureParser.canParse(line)) {
                    final Structure structure = structureParser.parse(currentLine, source);
                    structures.add(structure);

                    if (lastDocumentation != null) {
                        if (lastDocumentation instanceof RecordStructureDocumentation) {
                            recordStructureDocumentation.put(structure.getName(),
                                    (RecordStructureDocumentation) lastDocumentation);
                        } else {
                            logger.warn("CDOC warning");
                            logger.warn("The documentation immediately preceding 'record " + structure.getName()
                                    + "' is not valid record strucutre documentation.");
                        }
                    }
                    lastDocumentation = null;

                    currentLine = structureParser.getEndingIndex();
                } else if (scriptDocumentation == null && topLevelFactory.canParse(currentLine, source)) {
                    scriptDocumentation = topLevelFactory.parse(currentLine, source);

                    final int endingIndex = topLevelFactory.getEndingIndex();
                    if (endingIndex > currentLine) {
                        currentLine = endingIndex - 1;
                    }
                } else if (documentationParser.canParse(line)) {
                    lastDocumentation = documentationParser.parse(currentLine, source);

                    final int endingIndex = documentationParser.getEndingIndex();
                    if (endingIndex > currentLine) {
                        currentLine = endingIndex - 1;
                    }
                }
            }

            /*
             * Aggregate it all into an object
             */
            final List<Subroutine> subroutines = subroutineAggregator.aggregate(subroutineDeclarations,
                    subroutineDefinitions, subroutineDocumentation);
            final List<RecordStructure> recordStructures = recordStructureAggregator.aggregate(structures,
                    recordStructureDocumentation);

            return objectFactory.build(objectName, (D) scriptDocumentation, subroutines, recordStructures);
        } finally {
            point.collect();
        }
    }
}
