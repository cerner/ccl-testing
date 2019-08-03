package com.cerner.ftp.data.factory;

import java.net.URI;

import com.cerner.ftp.data.FileRequest;

/**
 * A factory to generate {@link FileRequest} objects.
 *
 * @author Joshua Hyde
 *
 */

public class FileRequestFactory {
    /**
     * Create a file request data object.
     *
     * @param sourceFile
     *            A {@link URI} object representing the location of the source file (the file to be downloaded or
     *            uploaded).
     * @param targetFile
     *            A {@link URI} object representing the location of the target file (the location to which a file is to
     *            be downloaded or uploaded).
     * @return A {@link FileRequest} object representing the desired file request metadata.
     * @throws NullPointerException
     *             If either of the given URIs are {@code null}.
     */
    public static FileRequest create(final URI sourceFile, final URI targetFile) {
        if (sourceFile == null) {
			throw new NullPointerException("Source file cannot be null.");
		}

        if (targetFile == null) {
			throw new NullPointerException("Target file cannot be null.");
		}

        return new FileRequestImpl(sourceFile, targetFile);
    }

    /**
     * An implementation of {@link FileRequest}.
     *
     * @author Joshua Hyde
     *
     */
    private static class FileRequestImpl implements FileRequest {
        private static final int HASH_CODE_SEED = 31;
        private final URI sourceFile;
        private final URI targetFile;

        /**
         * Create a file implementation.
         *
         * @param sourceFile
         *            A {@link URI} object representing the location of the source file (the file to be downloaded or
         *            uploaded).
         * @param targetFile
         *            A {@link URI} object representing the location of the target file (the location to which a file is
         *            to be downloaded or uploaded).
         */
        public FileRequestImpl(final URI sourceFile, final URI targetFile) {
            this.sourceFile = sourceFile;
            this.targetFile = targetFile;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == null) {
				return false;
			}

            if (!(o instanceof FileRequest)) {
				return false;
			}

            if (o == this) {
				return true;
			}

            final FileRequest other = (FileRequest) o;

            return this.getSourceFile().equals(other.getSourceFile())
                    && this.getTargetFile().equals(other.getTargetFile());
        }

        /*
         * (non-Javadoc)
         *
         * @see FileRequest#getSourceFile()
         */
		@Override
		public URI getSourceFile() {
            return sourceFile;
        }

        /*
         * (non-Javadoc)
         *
         * @see FileRequest#getTargetFile()
         */
		@Override
		public URI getTargetFile() {
            return targetFile;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;

            hashCode = HASH_CODE_SEED * hashCode + getSourceFile().hashCode();
            hashCode = HASH_CODE_SEED * hashCode + getTargetFile().hashCode();

            return hashCode;
        }
    }
}
