package com.cerner.ccl.testing.maven.ccl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.cerner.ccl.testing.maven.ccl.util.CclLogFileOutputStream;

/**
 * Unit tests for {@link CclLogFileOutputStream}.
 *
 * @author Joshua Hyde
 *
 */

public class CclLogFileOutputStreamTest {
    /**
     * Two output streams pointing to the same file should have the same hash code and be
     * {@link CclLogFileOutputStream#equals(Object) equal}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEquality() throws Exception {
        final File targetFile = new File("target/unit/testHashCode.txt");
        CclLogFileOutputStream outA = null;
        try {
            outA = new CclLogFileOutputStream(targetFile);
        } finally {
            /*
             * Close the stream so that outB won't get locked out of writing to the file.
             */
            if (outA != null)
                outA.close();
        }

        CclLogFileOutputStream outB = null;
        try {
            outB = new CclLogFileOutputStream(targetFile);
        } finally {
            /*
             * Close the stream so that future tests won't risk being interrupted by being locked out of the file.
             */
            if (outB != null)
                outB.close();
        }

        assertThat(outA.hashCode()).isEqualTo(outB.hashCode());
        assertThat(outA).isEqualTo(outB);
    }

    /**
     * Two streams that point to different files should not be equal.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testInequality() throws Exception {
        final File targetFileA = new File("target/unit/testHashCodeA.txt");
        final File targetFileB = new File("target/unit/testHashCodeB.txt");
        CclLogFileOutputStream outA = null;
        try {
            outA = new CclLogFileOutputStream(targetFileA);
        } finally {
            /*
             * Close the stream so that outB won't get locked out of writing to the file.
             */
            if (outA != null)
                outA.close();
        }

        CclLogFileOutputStream outB = null;
        try {
            outB = new CclLogFileOutputStream(targetFileB);
        } finally {
            /*
             * Close the stream so that future tests won't risk being interrupted by being locked out of the file.
             */
            if (outB != null)
                outB.close();
        }

        assertThat(outA.hashCode()).isNotEqualTo(outB.hashCode());
        assertThat(outA).isNotEqualTo(outB);
    }

    /**
     * Test the writing to a log file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWrite() throws Exception {
        final File destination = new File("target/unit/testWriteTo" + Long.toString(new Date().getTime()) + ".log");
        FileUtils.deleteQuietly(destination);

        final String line = "This is a line to be written to the file.";

        final CclLogFileOutputStream out = new CclLogFileOutputStream(destination);
        try {
            IOUtils.copy(new ByteArrayInputStream(line.getBytes("utf-8")), out);

            final List<String> lines = FileUtils.readLines(destination, "UTF-8");
            assertThat(lines.size()).isGreaterThanOrEqualTo(3);
            assertThat(lines.get(0)).isEqualTo("=============================================");
            assertThat(lines.get(1)).startsWith("Begin logging: ");
            assertThat(lines.get(2)).isEqualTo(line);
        } finally {
            out.close();
        }
    }

    /**
     * Verify that, if the same log file is opened multiple times, the output stream does not overwrite what was
     * previously written
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteMultiple() throws Exception {
        final File destination = new File(
                "target/unit/testWriteMultiple" + Long.toString(new Date().getTime()) + ".log");
        FileUtils.deleteQuietly(destination);

        new CclLogFileOutputStream(destination).close();
        new CclLogFileOutputStream(destination).close();
        new CclLogFileOutputStream(destination).close();
        new CclLogFileOutputStream(destination).close();

        assertThat(FileUtils.readLines(destination, "UTF-8").size()).isGreaterThanOrEqualTo(8);
    }
}
