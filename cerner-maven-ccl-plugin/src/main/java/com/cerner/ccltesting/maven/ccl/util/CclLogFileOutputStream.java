package com.cerner.ccltesting.maven.ccl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * An {@link OutputStream} that pipes input to a file.
 *
 * @author Joshua Hyde
 *
 */

public class CclLogFileOutputStream extends OutputStream {
    private final FileOutputStream out;
    private final File logFile;

    /**
     * Create a CCL log file.
     *
     * @param logFile
     *            A {@link File} object representing the location on the hard disk to which the log should be written.
     */
    public CclLogFileOutputStream(final File logFile) {
        this.logFile = logFile;
        try {
            FileUtils.forceMkdir(logFile.getParentFile());
            final BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8));
            try {
                writer.write("=============================================");
                writer.newLine();
                writer.write("Begin logging: " + new Date().toString());
                writer.newLine();
            } finally {
                writer.close();
            }

            out = new FileOutputStream(logFile, true);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to initialize CCL console log file.", e);
        }
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CclLogFileOutputStream other = (CclLogFileOutputStream) obj;
        if (logFile == null) {
            if (other.logFile != null)
                return false;
        } else if (!logFile.equals(other.logFile))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((logFile == null) ? 0 : logFile.hashCode());
        return result;
    }

    @Override
    public void write(final int b) throws IOException {
        out.write(b);
    }
}
