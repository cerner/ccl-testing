package com.cerner.ccl.cdoc.velocity.navigation;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing the navigation between pages in the report.
 *
 * @author Joshua Hyde
 *
 */

public class Navigation {
    private final String destination;
    private final String anchorText;

    /**
     * Create a navigation.
     *
     * @param anchorText
     *            The text of the anchor.
     * @param destination
     *            The destination of the navigation.
     * @throws IllegalArgumentException
     *             If either of the given objects are {@code null}.
     */
    public Navigation(final String anchorText, final String destination) {
        if (anchorText == null) {
            throw new IllegalArgumentException("Anchor text cannot be null.");
        }

        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null.");
        }

        this.anchorText = anchorText;
        this.destination = destination;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Navigation)) {
            return false;
        }

        final Navigation other = (Navigation) obj;
        return getAnchorText().equals(other.getAnchorText()) && getDestination().equals(other.getDestination());
    }

    /**
     * Get the anchor text.
     *
     * @return The text of the anchor.
     */
    public String getAnchorText() {
        return anchorText;
    }

    /**
     * Get the destination.
     *
     * @return The destination.
     */
    public String getDestination() {
        return destination;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + anchorText.hashCode();
        result = prime * result + destination.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
