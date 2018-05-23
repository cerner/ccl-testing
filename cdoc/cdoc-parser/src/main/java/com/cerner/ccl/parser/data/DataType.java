package com.cerner.ccl.parser.data;

import java.util.Locale;

import com.cerner.ccl.parser.exception.InvalidDataTypeDeclarationException;

/**
 * Enumerations of the possible data types within CCL.
 *
 * @author Joshua Hyde
 *
 */

public enum DataType {
    /**
     * A fixed-length character variable.
     */
    CHAR,
    /**
     * A single precision floating point variable.
     */
    F4,
    /**
     * A double precision floating point variable.
     */
    F8,
    /**
     * A one-byte integer.
     */
    I1,
    /**
     * A one-byte unsigned integer.
     */
    UI1,
    /**
     * A two-byte integer.
     */
    I2,
    /**
     * A two-byte unsigned integer.
     */
    UI2,
    /**
     * A four-byte integer.
     */
    I4,
    /**
     * A four-byte unsigned integer.
     */
    UI4,
    /**
     * An eight-byte integer.
     */
    W8,
    /**
     * An eight-byte unsigned integer.
     */
    UW8,
    /**
     * An OS dependent 32/64-bit integer.
     */
    H,
    /**
     * A varchar string.
     */
    VC,
    /**
     * An as-is character string (without encoding)
     */
    GVC,
    /**
     * A variable-length CLOB.
     */
    ZVC,
    /**
     * A variable-length raw character BLOB.
     */
    ZGVC,
    /**
     * A quadword date.
     */
    DQ8,
    /**
     * A 12-byte time stamp.
     */
    DM12,
    /**
     * A 14-byte time stamp with time zone.
     */
    DM14;

    /**
     * Resolve a data type declaration to an enumeration.
     *
     * @param declaration
     *            The textual declaration of data type that is to be resolved.
     * @return A {@link DataType} enum corresponding to the given declaration.
     * @throws IllegalArgumentException
     *             If the given declaration is {@code null}.
     * @throws InvalidDataTypeDeclarationException
     *             If the given declaration cannot be resolved to an enum.
     */
    public static DataType forDeclaration(final String declaration) {
        if (declaration == null) {
            throw new IllegalArgumentException("Declaration cannot be null.");
        }

        final String normalized = declaration.toUpperCase(Locale.US);
        if (normalized.startsWith("C")) {
            return CHAR;
        }

        for (final DataType type : values()) {
            if (type.name().equals(normalized)) {
                return type;
            }
        }

        throw new InvalidDataTypeDeclarationException(
                "Unrecognized declaration: " + declaration + "; normalized to " + normalized);
    }
}
