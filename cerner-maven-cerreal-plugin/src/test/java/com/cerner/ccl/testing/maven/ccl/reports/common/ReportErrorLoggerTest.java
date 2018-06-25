package com.cerner.ccl.testing.maven.ccl.reports.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.testing.maven.ccl.reports.common.ReportErrorLogger;

/**
 * Unit tests for {@link ReportErrorLogger}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { File.class, FileUtils.class, ReportErrorLogger.class })
public class ReportErrorLoggerTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private File logDirectory;
    @Mock
    private File logFile;
    private ReportErrorLogger logger;

    /**
     * Set up the logger for each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Before
    public void setUp() throws Exception {
        whenNew(File.class).withArguments(logDirectory, "reportErrorLog.log").thenReturn(logFile);

        logger = new ReportErrorLogger(logDirectory);
    }

    /**
     * Construction with a {@code null} log directory should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullLogDirectory() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Log directory cannot be null.");
        new ReportErrorLogger(null);
    }

    /**
     * Verify the writing of error data.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLogFailedTransformation() throws Exception {
        final String xmlFileAbsolutePath = "i am the xml file absolute path";
        final File xmlFile = mock(File.class);
        when(xmlFile.getAbsolutePath()).thenReturn(xmlFileAbsolutePath);
        whenNew(File.class).withArguments(logDirectory, "badTransformXml.xml").thenReturn(xmlFile);

        final String xslFileAbsolutePath = "i am the XSL file absolute path";
        final File xslFile = mock(File.class);
        when(xslFile.getAbsolutePath()).thenReturn(xslFileAbsolutePath);
        whenNew(File.class).withArguments(logDirectory, "badTransformXsl.xml").thenReturn(xslFile);

        final String xml = "i am the xml";
        final String xsl = "i am the xsl";

        mockStatic(FileUtils.class);
        logger.logFailedTransformation(xml, xsl);

        verifyStatic(FileUtils.class);
        FileUtils.writeStringToFile(logFile,
                "An attempt to run an XSLT transformation has failed\n" + "The xml file has been written out to "
                        + xmlFileAbsolutePath + "\n" + "The xsl file has been written out to " + xslFileAbsolutePath
                        + "\n",
                "utf-8");

        verifyStatic(FileUtils.class);
        FileUtils.writeStringToFile(xslFile, xsl, "utf-8");

        verifyStatic(FileUtils.class);
        FileUtils.writeStringToFile(xmlFile, xml, "utf-8");
    }
}
