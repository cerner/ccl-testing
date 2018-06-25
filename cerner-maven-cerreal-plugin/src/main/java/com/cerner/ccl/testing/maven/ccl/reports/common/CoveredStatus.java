package com.cerner.ccl.testing.maven.ccl.reports.common;

/**
 * Enumerations of the possible coverage states of a line.
 *
 * @author Jeff Wiedemann
 * @author Joshua Hyde
 *
 */
public enum CoveredStatus {
    /**
     * The coverage is not defined.
     */
    UNDEFINED('Z'),
    /**
     * The line was not covered (i.e., not executed)
     */
    NOT_COVERED('U'),
    /**
     * The line was covered (i.e., executed)
     */
    COVERED('C'),
    /**
     * The line was skipped (i.e., executed)
     */
    SKIPPED('S'),
    /**
     * The line is not executable and cannot be covered
     */
    NOT_EXECUTABLE('N');

    /**
     * Get a coverage status for a given character representation.
     *
     * @param characterRepresentation
     *            The character representation to be resolved.
     * @return A {@link CoveredStatus} corresponding to the given character representation.
     * @throws IllegalArgumentException
     *             If the given character representation is unknown.
     * @see #getCharacterRepresentation()
     */
    public static CoveredStatus forCharacterRepresentation(String characterRepresentation) {
        for (CoveredStatus status : values())
            if (status.getCharacterRepresentation().equals(characterRepresentation))
                return status;

        throw new IllegalArgumentException("Unknown character representation: " + characterRepresentation);
    }

    private final String characterRepresentation;

    /**
     * Create a covered status.
     *
     * @param characterRepresentation
     *            A single-character representation of the status.
     */
    private CoveredStatus(char characterRepresentation) {
        this.characterRepresentation = String.valueOf(characterRepresentation);
    }

    /**
     * Get the single-character representation of this status.
     *
     * @return The single-character representation of this status.
     */
    public String getCharacterRepresentation() {
        return characterRepresentation;
    }
}
