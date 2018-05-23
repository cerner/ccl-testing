package com.cerner.ccltesting.maven.ccl.reports.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * A utility class for reporting errors.
 * 
 * @author Jeff Wiedemann
 * 
 */

public class ReportErrorLogger {
    private final File logDirectory;
    private final File logFile;

    /**
     * Create a report error logger.
     * 
     * @param logDirectory
     *            The directory to which the log should be written.
     */
    public ReportErrorLogger(File logDirectory) {
        if (logDirectory == null)
            throw new IllegalArgumentException("Log directory cannot be null.");

        this.logDirectory = logDirectory;
        this.logFile = new File(logDirectory, "reportErrorLog.log");
    }

    /**
     * Log the failure of an XSL stylesheet transformation of XML.
     * 
     * @param xml
     *            The XML that was attempted to be transformed.
     * @param xsl
     *            The XSL that was attempted to be applied to the given XML.
     */
    public void logFailedTransformation(String xml, String xsl) {
        final File xmlFile = new File(logDirectory, "badTransformXml.xml");
        final File xslFile = new File(logDirectory, "badTransformXsl.xml");

        final StringBuilder fileText = new StringBuilder();
        fileText.append("An attempt to run an XSLT transformation has failed\n");
        fileText.append("The xml file has been written out to " + xmlFile.getAbsolutePath() + "\n");
        fileText.append("The xsl file has been written out to " + xslFile.getAbsolutePath() + "\n");

        try {
            FileUtils.writeStringToFile(logFile, fileText.toString(), "utf-8");
            FileUtils.writeStringToFile(xmlFile, xml, "utf-8");
            FileUtils.writeStringToFile(xslFile, xsl, "utf-8");
        } catch (IOException e) {
            // TODO: better error-reporting
            e.printStackTrace();
        }
    }
}
