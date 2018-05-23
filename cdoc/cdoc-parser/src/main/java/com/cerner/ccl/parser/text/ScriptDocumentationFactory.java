package com.cerner.ccl.parser.text;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.text.documentation.parser.ScriptDocumentationParser;

/**
 * A factory that produces {@link ScriptDocumentation} objects.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptDocumentationFactory implements TopLevelDocumentationFactory<ScriptDocumentation> {
    private final ScriptDocumentationParser parser;

    /**
     * Create a factory.
     */
    public ScriptDocumentationFactory() {
        this(new ScriptDocumentationParser());
    }

    /**
     * Create a factory with a specific parser.
     *
     * @param parser
     *            A {@link ScriptDocumentationParser} used to parse data.
     * @throws IllegalArgumentException
     *             If the given parser is {@code null}.
     */
    public ScriptDocumentationFactory(final ScriptDocumentationParser parser) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser cannot be null.");
        }

        this.parser = parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final int currentIndex, final List<String> source) {
        int previousNonEmptyIndex = -1;
        for (int previousIndex = currentIndex - 1; previousIndex > -1; previousIndex--) {
            if (!StringUtils.strip(source.get(previousIndex)).isEmpty()) {
                previousNonEmptyIndex = previousIndex;
                break;
            }
        }
        return parser.canParse(source.get(currentIndex)) && previousNonEmptyIndex > -1 && StringUtils
                .startsWithIgnoreCase(StringUtils.strip(source.get(previousNonEmptyIndex)), "CREATE PROGRAM ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndingIndex() {
        return parser.getEndingIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScriptDocumentation parse(final int currentIndex, final List<String> source) {
        return parser.parse(currentIndex, source);
    }

}
