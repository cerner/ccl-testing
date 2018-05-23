package com.cerner.ccl.parser.text;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.IncludeDocumentation;
import com.cerner.ccl.parser.text.smoosh.DocumentationBlockIndexedSmoosher;

/**
 * A factory used to produce {@code IncludeDocumentation} objects.
 *
 * @author Joshua Hyde
 *
 */

public class IncludeDocumentationFactory implements TopLevelDocumentationFactory<IncludeDocumentation> {
    private final DocumentationBlockIndexedSmoosher smoosher;

    /**
     * Create a factory.
     */
    public IncludeDocumentationFactory() {
        this(new DocumentationBlockIndexedSmoosher());
    }

    /**
     * Create a factory with a smoosher.
     *
     * @param smoosher
     *            A {@link DocumentationBlockIndexedSmoosher} used to smoosh and parse the documentation.
     * @throws IllegalArgumentException
     *             If the given smoosher is {@code null}.
     */
    public IncludeDocumentationFactory(final DocumentationBlockIndexedSmoosher smoosher) {
        if (smoosher == null) {
            throw new IllegalArgumentException("Smoosher cannot be null.");
        }

        this.smoosher = smoosher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final int currentIndex, final List<String> source) {
        boolean canParse = true;
        if (currentIndex > 0) {
            for (int i = currentIndex - 1; canParse && i >= 0; i--) {
                final String line = source.get(i);
                canParse &= (StringUtils.startsWithIgnoreCase(line, "%#define ")
                        || StringUtils.startsWithIgnoreCase(line, "%#def ")
                        || StringUtils.startsWithIgnoreCase(line, "%#ifndef ") || StringUtils.isBlank(line));
            }
        }
        return canParse && StringUtils.startsWith(source.get(currentIndex), "/**");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEndingIndex() {
        return smoosher.getEndingIndex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncludeDocumentation parse(final int currentIndex, final List<String> source) {
        // currentIndex+1 because currentIndex is assumed to be "/**"
        return new IncludeDocumentation(smoosher.smoosh(currentIndex + 1, source));
    }

}
