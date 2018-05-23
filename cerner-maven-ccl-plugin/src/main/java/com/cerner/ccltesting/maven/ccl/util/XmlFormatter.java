package com.cerner.ccltesting.maven.ccl.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * A utility class to format XML into a more readable format.
 *
 * @author Joshua Hyde
 *
 */

public class XmlFormatter {
    /**
     * Format a given XML string.
     *
     * @param xml
     *            The XML to be formatted.
     * @param file
     *            A {@link File} representing the directory to which the XML is to be written.
     */
    public void formatAndWriteXml(final String xml, final File file) {
        final OutputFormat formatter = OutputFormat.createPrettyPrint();
        formatter.setIndentSize(4);

        final StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(writer, formatter);
            xmlWriter.write(DocumentHelper.parseText(xml));
            xmlWriter.flush();
        } catch (final IOException e) {
            throw new RuntimeException("Failed to serialize XML data in formatting.", e);
        } catch (final DocumentException e) {
            throw new RuntimeException("Failed to properly parse XML document.", e);
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                    FileUtils.writeStringToFile(file, xml, "utf-8");
                } catch (final IOException e) {
                    // swallow
                }
            }
        }
        try {
            FileUtils.writeStringToFile(file, writer.toString(), "utf-8");
        } catch (final IOException e) {
            throw new RuntimeException("Failed to write formatted XML to file " + file, e);
        }
    }
}
