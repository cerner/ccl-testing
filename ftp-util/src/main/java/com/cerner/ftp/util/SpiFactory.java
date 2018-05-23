package com.cerner.ftp.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.discovery.tools.Service;

/**
 * Utility class to look up implementations of service providers.
 *
 * @author Joshua Hyde
 *
 */

public class SpiFactory {
    private static final Map<Class<?>, Object> overrides = new HashMap<Class<?>, Object>();

    /**
     * Set an override of the provider lookup.
     *
     * @param <T>
     *            The type of the object to be overridden.
     * @param clazz
     *            The {@link Class} whose lookup is to be overridden.
     * @param object
     *            The object to be used by the provider lookup instead of its normal lookup.
     * @return If an override previously existed, the object that was previously used in the override; otherwise,
     *         {@code null}.
     */
    @SuppressWarnings("unchecked")
    public static <T> T override(final Class<T> clazz, final T object) {
        return (T) overrides.put(clazz, object);
    }

    /**
     * Remove an override.
     *
     * @param clazz
     *            The {@link Class} whose override is to be removed.
     */
    public static void removeOverride(final Class<?> clazz) {
        overrides.remove(clazz);
    }

    /**
     * Get a service provider.
     *
     * @param <T>
     *            The type of the object to be returned.
     * @param clazz
     *            The {@link Class} whose implementation is to be returned.
     * @return The implementation of the given class.
     * @throws IllegalStateException
     *             If no providers are found for the given class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProvider(final Class<T> clazz) {
        if (overrides.containsKey(clazz))
            return (T) overrides.get(clazz);

        final Enumeration<T> providers = Service.providers(clazz);
        if (!providers.hasMoreElements())
            throw new IllegalStateException("No providers found for: " + clazz.getName());

        return providers.nextElement();
    }
}
