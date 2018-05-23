package com.cerner.ccl.parser.text.smoosh;

/**
 * A documentation smoosher that is index-driven. This is ideal for when smooshing of multiple segments within a
 * documentation block is needed.
 *
 * @author Joshua Hyde
 */

public class DocumentationTagIndexedSmoosher extends AbstractDocumentationBlockIndexedSmoosher {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSmoosh(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return isTagOpen(line);
    }
}
