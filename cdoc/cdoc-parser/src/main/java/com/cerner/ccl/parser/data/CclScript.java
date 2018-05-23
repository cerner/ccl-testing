package com.cerner.ccl.parser.data;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Definition of a CCL script's documentation.
 *
 * @author Joshua Hyde
 *
 */

public class CclScript implements Named {
    private final String scriptName;
    private final ScriptDocumentation scriptDocumentation;
    private final List<Subroutine> subroutines;
    private final List<RecordStructure> recordStructures;

    /**
     * Create a CCL script.
     *
     * @param scriptName
     *            The name of the CCL script.
     * @param scriptDocumentation
     *            A {@link ScriptDocumentation} object representing the script-level documentation of this script.
     * @param subroutines
     *            A {@link List} of {@link Subroutine} objects representing the subroutines defined and described in the
     *            CCL script. If {@code null}, then this object will internally store an empty list.
     * @param recordStructures
     *            A {@link List} of {@link RecordStructure} objects representing the record structures defined and
     *            described in the CCL script. If {@code null}, then this object will internally store an empty list.
     * @throws IllegalArgumentException
     *             If any of the given arguments are {@code null}.
     */
    public CclScript(final String scriptName, final ScriptDocumentation scriptDocumentation,
            final List<Subroutine> subroutines, final List<RecordStructure> recordStructures) {
        if (scriptName == null) {
            throw new IllegalArgumentException("Script name cannot be null.");
        }

        if (scriptDocumentation == null) {
            throw new IllegalArgumentException("Script documentation cannot be null.");
        }

        if (subroutines == null) {
            throw new IllegalArgumentException("Subroutines cannot be null.");
        }

        if (recordStructures == null) {
            throw new IllegalArgumentException("Record structures cannot be null.");
        }

        this.scriptName = scriptName;
        this.scriptDocumentation = scriptDocumentation;
        this.subroutines = Collections.unmodifiableList(subroutines);
        this.recordStructures = Collections.unmodifiableList(recordStructures);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof CclScript)) {
            return false;
        }

        final CclScript other = (CclScript) obj;
        return new EqualsBuilder().append(scriptName, other.scriptName)
                .append(scriptDocumentation, other.scriptDocumentation).append(recordStructures, other.recordStructures)
                .append(subroutines, other.subroutines).isEquals();
    }

    /**
     * Get the list of record structures defined and described in this CCL script.
     *
     * @return An immutable {@link List} of {@link RecordStructure} objects that are defined and described in this CCL
     *         script.
     */
    public List<RecordStructure> getRecordStructures() {
        return recordStructures;
    }

    /**
     * Get the script-level documentation of this CCL script.
     *
     * @return The script-level documentation of this CCL script.
     */
    public ScriptDocumentation getScriptDocumentation() {
        return scriptDocumentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return scriptName;
    }

    /**
     * Get the subroutines defined within the CCL script.
     *
     * @return An immutable {@link List} of {@link Subroutine} objects representing the defined subroutines.
     */
    public List<Subroutine> getSubroutines() {
        return subroutines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + recordStructures.hashCode();
        result = prime * result + scriptDocumentation.hashCode();
        result = prime * result + scriptName.hashCode();
        result = prime * result + subroutines.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
