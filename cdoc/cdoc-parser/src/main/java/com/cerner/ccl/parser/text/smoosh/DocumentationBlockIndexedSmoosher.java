package com.cerner.ccl.parser.text.smoosh;

/**
 * An {@link AbstractDocumentationBlockIndexedSmoosher} that can smoosh anything.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentationBlockIndexedSmoosher extends AbstractDocumentationBlockIndexedSmoosher {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSmoosh(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return true;
    }
}
