package com.cerner.ccl.parser.data;

import java.util.Collections;
import java.util.List;

import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Definition of an include file.
 *
 * @author Joshua Hyde
 *
 */

public class IncludeFile {
    private final String objectName;
    private final IncludeDocumentation includeDocumentation;
    private final List<Subroutine> subroutines;
    private final List<RecordStructure> recordStructures;

    /**
     * Create an include file.
     *
     * @param objectName
     *            The name of the object. This should include the file extension, such as "include.inc" or "subs.sub".
     * @param includeDocumentation
     *            An {@link IncludeDocumentation} representing the top-level documentation of the file.
     * @param subroutines
     *            A {@link List} of {@link Subroutine} objects representing the subroutines defined within the include
     *            file.
     * @param recordStructures
     *            A {@link List} of {@link RecordStructure} objects representing the record structures defined within
     *            the include file.
     */
    public IncludeFile(final String objectName, final IncludeDocumentation includeDocumentation,
            final List<Subroutine> subroutines, final List<RecordStructure> recordStructures) {
        if (objectName == null) {
            throw new IllegalArgumentException("Object name cannot be null.");
        }

        if (includeDocumentation == null) {
            throw new IllegalArgumentException("Include file documentation cannot be null.");
        }

        if (subroutines == null) {
            throw new IllegalArgumentException("Subroutines cannot be null.");
        }

        if (recordStructures == null) {
            throw new IllegalArgumentException("Record structures cannot be null.");
        }

        this.objectName = objectName;
        this.includeDocumentation = includeDocumentation;
        this.subroutines = Collections.unmodifiableList(subroutines);
        this.recordStructures = Collections.unmodifiableList(recordStructures);
    }

    /**
     * Get the top-level documentation of the include file.
     *
     * @return A {@link IncludeDocumentation} representing the top-level documentation of the include file.
     */
    public IncludeDocumentation getIncludeDocumentation() {
        return includeDocumentation;
    }

    /**
     * Get the name of the include file.
     *
     * @return The name of the include file. This will contain the file extension.
     */
    public String getName() {
        return objectName;
    }

    /**
     * Get the record structures defined within this include file.
     *
     * @return An immutable {@link List} of {@link RecordStructure} objects representing the record structures defined
     *         within this include file.
     */
    public List<RecordStructure> getRecordStructures() {
        return recordStructures;
    }

    /**
     * Get the subroutines defined within this include file.
     *
     * @return An immutable {@link List} of {@link Subroutine} objects representing the subroutines defined within this
     *         include file.
     */
    public List<Subroutine> getSubroutines() {
        return subroutines;
    }
}
