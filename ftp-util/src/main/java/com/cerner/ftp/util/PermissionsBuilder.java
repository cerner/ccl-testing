package com.cerner.ftp.util;

/**
 * A utility class to translate UNIX bitwise representations of permissions into a single collective numeric value.
 *
 * @author Joshua Hyde
 *
 */

public final class PermissionsBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private PermissionsBuilder() {

    }

    /**
     * Create an integer representing the cumulative permissions on a file. <br>
     * These values follow UNIX bitwise representation of permission:
     * <ul>
     * <li>7: read, write, execute</li>
     * <li>6: read, write</li>
     * <li>5: read, execute</li>
     * <li>4: read</li>
     * <li>3: write, execute</li>
     * <li>2: write</li>
     * <li>1: execute</li>
     * <li>0: no permissions</li>
     * </ul>
     *
     * @param owner
     *            The value of permissions for the owner of the file.
     * @param group
     *            The value of permissions for the group owner of the file.
     * @param other
     *            The value of permissions for all users that are not the owner and are not in the owner group.
     * @return An {@code int} representing the bitwise sum of the given permissions.
     */
    public static int build(final int owner, final int group, final int other) {
        int perms = owner;
        perms <<= 3;
        perms |= group;
        perms <<= 3;
        perms |= other;
        return perms;
    }
}
