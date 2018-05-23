package com.cerner.ccl.cdoc.mojo.data;

import java.io.File;
import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing an object that is to be documented.
 *
 * @author Joshua Hyde
 *
 */

public class Documentation {
    private final File sourceFile;
    private final ObjectType objectType;
    private final String objectName;
    private final String objectFilename;
    private final String destinationFilename;

    /**
     * Create a documentation object.
     *
     * @param sourceFile
     *            A {@link File} representing the location of the source code to be parsed (eventually).
     * @throws IllegalArgumentException
     *             If the given source file is {@code null}.
     */
    public Documentation(final File sourceFile) {
        if (sourceFile == null) {
            throw new IllegalArgumentException("Source file cannot be null.");
        }

        this.sourceFile = sourceFile;
        this.objectFilename = sourceFile.getName().toLowerCase(Locale.US);
        this.objectType = determineObjectType(sourceFile);
        this.objectName = objectType == ObjectType.PRG ? objectFilename.substring(0, objectFilename.lastIndexOf('.'))
                : objectFilename;

        switch (this.objectType) {
        case INC:
            this.destinationFilename = objectName.substring(0, objectName.lastIndexOf('.')) + "-inc.html";
            break;
        case PRG:
            this.destinationFilename = objectName + "-prg.html";
            break;
        case SUB:
            this.destinationFilename = objectName.substring(0, objectName.lastIndexOf('.')) + "-sub.html";
            break;
        default:
            throw new IllegalArgumentException("Unrecognized object type: " + this.objectType);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Documentation)) {
            return false;
        }

        final Documentation other = (Documentation) obj;
        return getSourceFile().equals(other.getSourceFile());
    }

    /**
     * Get the name of the file to which the documentation should be written.
     *
     * @return The name of the file to which the documentation should be written.
     */
    public String getDestinationFilename() {
        return destinationFilename;
    }

    /**
     * Get the filename of the object.
     *
     * @return The filename of the object.
     */
    public String getObjectFilename() {
        return objectFilename;
    }

    /**
     * Get the name of the object.
     *
     * @return The name of the object.
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Get the type of object that is to be documented.
     *
     * @return An {@link ObjectType} enum representing the type of object to be documented.
     */
    public ObjectType getObjectType() {
        return objectType;
    }

    /**
     * Get the source file.
     *
     * @return A {@link File} object representing a reference to the file containing the source code.
     */
    public File getSourceFile() {
        return sourceFile;
    }

    @Override
    public int hashCode() {
        return getSourceFile().hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Determine the object based on the file extension of the given file.
     *
     * @param sourceFile
     *            A {@link File} representing the file containing the source code of the object to be documented.
     * @return An {@link ObjectType} enum indicating the type of object to be documented.
     * @throws IllegalArgumentException
     *             If the given file has an unknown extension.
     */
    private ObjectType determineObjectType(final File sourceFile) {
        final String filename = sourceFile.getAbsolutePath().toUpperCase(Locale.US);
        if (filename.endsWith(".PRG")) {
            return ObjectType.PRG;
        } else if (filename.endsWith(".INC")) {
            return ObjectType.INC;
        } else if (filename.endsWith(".SUB")) {
            return ObjectType.SUB;
        }
        throw new IllegalArgumentException("Unrecognized file type: " + sourceFile.getAbsolutePath());
    }
}
