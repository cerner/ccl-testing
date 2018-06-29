package com.cerner.ccl.analysis.mojo.exclusions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.Builder;
import com.cerner.ccl.analysis.mojo.exclusions.jaxb.Exclusion;
import com.cerner.ccl.analysis.mojo.exclusions.jaxb.Exclusions;

/**
 * This maintains a chain of {@link ViolationFilterChain} objects that can be used to determine whether or not a
 * {@link Violation} qualifies for exclusion based on a user-set criteria.
 * 
 * @author Joshua Hyde
 * 
 */

public class ViolationFilterEngine {
    private final Collection<ViolationFilterChain> filterChain = new ArrayList<ViolationFilterChain>();

    /**
     * Create an engine.
     * 
     * @param exclusions
     *            An {@link Exclusions} object representing the set of user-configured {@link Violation} exclusions.
     * @throws IllegalArgumentException
     *             If the given {@link Exclusions} object is {@code null}.
     */
    public ViolationFilterEngine(final Exclusions exclusions) {
        if (exclusions == null)
            throw new IllegalArgumentException("Exclusions cannot be null.");

        for (final Exclusion exclusion : exclusions.getExclusion()) {
            final Builder builder = ViolationFilterChain.build();

            if (exclusion.getScriptName() != null)
                builder.withScriptName(exclusion.getScriptName());

            if (exclusion.getViolationId() != null)
                builder.withViolationId(exclusion.getViolationId());

            if (exclusion.getLineNumber() != null)
                builder.withLineNumber(exclusion.getLineNumber().intValue());

            if (exclusion.getSubroutineName() != null)
                builder.withSubroutineName(exclusion.getSubroutineName());

            if (exclusion.getVariableName() != null)
                builder.withVariableName(exclusion.getVariableName());

            filterChain.add(builder.build());
        }
    }

    /**
     * Determine whether or not a given violation has been configured to be ignored.
     * 
     * @param scriptName
     *            The name of the script to which the violation belongs.
     * @param violation
     *            The {@link Violation} to be considered for exclusion.
     * @return {@code true} if the given {@link Violation} should be ignored; {@code false} if not.
     */
    public boolean remove(final String scriptName, final Violation violation) {
        boolean remove = false;
        final Iterator<ViolationFilterChain> chainIt = filterChain.iterator();
        while (!remove && chainIt.hasNext()) {
            remove = chainIt.next().exclude(scriptName, violation);
        }
        return remove;
    }
}
