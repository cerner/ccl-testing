package com.cerner.ccl.j4ccl.impl.adders;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Skeleton implementation of common functions used by CCL compiler command adders.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractCompilerAdder {
    private final Set<File> dependencies = new HashSet<File>();
    private File listingLocation;
    private boolean debugCompile;

    /**
     * Add a file that must be on the server prior to successful compilation.
     *
     * @param dependency
     *            A {@link File} object representing a dependent file.
     * @throws NullPointerException
     *             If the given object is {@code null}.
     */
    protected void addDependency(final File dependency) {
        if (dependency == null)
            throw new NullPointerException("Dependent file cannot be a null reference.");

        dependencies.add(dependency);
    }

    /**
     * Get whether or not the script should be compiled in debug mode.
     *
     * @return {@code true} if the script should be compiled in debug mode; {@code false} if not. By default, this is
     *         {@code false}.
     */
    protected boolean doDebugCompile() {
        return debugCompile;
    }

    /**
     * Get the list of dependencies set for this compiler.
     *
     * @return A {@link Collection} of {@link File} objects representing the dependencies that must exist on the remote
     *         server prior to compilation.
     */
    protected Collection<File> getDependencies() {
        return dependencies;
    }

    /**
     * Get the location on the local hard drive to where the compilation listing output should be placed.
     *
     * @return A {@link File} object representing the hard disk location.
     */
    protected File getListingLocation() {
        return listingLocation;
    }

    /**
     * Determine whether or not a listing location has been provided.
     *
     * @return {@code true} if a listing location has been specified; {@code
     *         false} if it has not.
     */
    protected boolean hasListingLocationDefined() {
        return listingLocation != null;
    }

    /**
     * Set whether or not the script should be compiled in debug mode.
     *
     * @param debugCompile
     *            {@code true} if the script should be compiled in debug mode; {@code false} if not.
     */
    protected void setDoDebugCompile(final boolean debugCompile) {
        this.debugCompile = debugCompile;
    }

    /**
     * Set the location on the local hard drive to where the compilation listing output should be placed.
     *
     * @param listingLocation
     *            A {@link File} object representing the hard disk location.
     * @throws NullPointerException
     *             If the given listing location is {@code null}.
     */
    protected void setListingLocation(final File listingLocation) {
        if (listingLocation == null)
            throw new NullPointerException("Listing location cannot be null.");

        this.listingLocation = listingLocation;
    }
}
