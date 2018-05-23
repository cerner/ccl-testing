package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.data.ViolationId;

/**
 * An extension of {@link ViolationId} that automatically namespaces the given identifier to a "CORE" namespace.
 * 
 * @author Joshua Hyde
 * 
 */

public class CoreViolationId extends ViolationId {
    /**
     * The core rule {@link ViolationId#getNamespace() namespace}.
     */
    public static final String NAMESPACE = "CORE";

    /**
     * Create a core violation ID.
     * 
     * @param namespacedIdentifier
     *            The ID of the violation which, scoped to the "CORE" namespace, uniquely identifies a violation.
     */
    public CoreViolationId(final String namespacedIdentifier) {
        super(NAMESPACE, namespacedIdentifier);
    }

}
