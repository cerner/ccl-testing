package com.cerner.ccl.analysis.mojo.exclusions.filters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * Skeleton definition of a {@link ViolationFilter} that can use reflections to inspect a violation for a getter and
 * field matching a property name.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractPropertyReflectingFilter implements ViolationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPropertyReflectingFilter.class);

    /**
     * Reflect upon a violation for a property.
     *
     * @param <T>
     *            The violation type.
     * @param violation
     *            The {@link Violation} upon which reflection is to be done.
     * @param propertyName
     *            The name of the property to be retrieved. A getter will first be checked - following the Java bean
     *            naming convention - and then an internal field matching the given name will be looked for.
     * @return {@code null} if no property matching the name could be found; otherwise, the value stored within the
     *         given violation.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getInternalValue(final Violation violation, final String propertyName) {
        final String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            final Method getter = violation.getClass().getDeclaredMethod(methodName);
            if (!getter.isAccessible()) {
                getter.setAccessible(true);
            }
            return (T) getter.invoke(violation);
        } catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOGGER.trace("Failed to retrieve '{}' off of {}; moving on to field retrieval.", methodName, violation);
            LOGGER.trace("Tracing thrown exception during retrieval by method.", e);
        }

        try {
            final Field field = violation.getClass().getDeclaredField(propertyName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return (T) field.get(violation);
        } catch (final IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            LOGGER.trace("Failed to retrieve field by name '{}' off of {}.", propertyName, violation);
            LOGGER.trace("Tracing thrown exception during retrieval by field.", e);
        }

        return null;
    }

}
