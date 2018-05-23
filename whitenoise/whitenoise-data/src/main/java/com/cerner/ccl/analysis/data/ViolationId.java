package com.cerner.ccl.analysis.data;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This object is used to uniquely identify a violation. It is composed of two parts in an attempt to uniquely identify
 * a violation: a namespace, which is used to logically group related rules together, and a namespaced identifier, which
 * should be unique within the scope of the given namespace.
 * <p>
 * All given identifiers are normalized to upper-case using a {@link Locale#US US} locale.
 *
 * @author Joshua Hyde
 *
 */

public class ViolationId {
    private final String namespace;
    private final String namespacedIdentifier;

    /**
     * Create a violation ID.
     *
     * @param namespace
     *            The namespace which scopes the given namespaced identifier to uniqueness.
     * @param namespacedIdentifier
     *            The identifier which, within the scope of the given namespace, uniquely identifies a violation.
     * @throws IllegalArgumentException
     *             If any of the given strings are blank or {@code null}.
     */
    public ViolationId(final String namespace, final String namespacedIdentifier) {
        if (StringUtils.isBlank(namespace))
            throw new IllegalArgumentException("Invalid namespace; given namespace was [" + (namespace == null ? "<null>" : namespace) + "]");

        if (StringUtils.isBlank(namespacedIdentifier))
            throw new IllegalArgumentException("Invalid namespaced identifier; given identifier was [" + (namespacedIdentifier == null ? "<null>" : namespacedIdentifier) + "]");

        this.namespace = namespace.toUpperCase(Locale.US);
        this.namespacedIdentifier = namespacedIdentifier.toUpperCase(Locale.US);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof ViolationId))
            return false;

        return getIdentifier().equalsIgnoreCase(((ViolationId) obj).getIdentifier());
    }

    /**
     * Get the identifier, which is composed of both the given {@link #getNamespace() namespace} and {@link #getNamespacedIdentifier() identifier}.
     *
     * @return The identifier of a violation.
     */
    public String getIdentifier() {
        return getNamespace() + "." + getNamespacedIdentifier();
    }

    /**
     * Get the namespace used to scope this ID to uniqueness.
     *
     * @return This ID's namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Get the identifier that, within the given namespace, uniquely identifies a violation.
     *
     * @return The relatively-unique identifier of a violation.
     */
    public String getNamespacedIdentifier() {
        return namespacedIdentifier;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIdentifier()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
