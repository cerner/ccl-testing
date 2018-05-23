package com.cerner.ccltesting.maven.ccl.util;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test of {@link XmlFormatter}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DocumentHelper.class, FileUtils.class, OutputFormat.class, StringWriter.class,
        XmlFormatter.class, XMLWriter.class })
public class XmlFormatterTest {
    /**
     * Test the formatting and writing of XML.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormatAndWriteXml() throws Exception {
        final String xml = "i am the xml";
        final File outputFile = mock(File.class);

        final OutputFormat outputFormat = mock(OutputFormat.class);
        mockStatic(OutputFormat.class);
        when(OutputFormat.createPrettyPrint()).thenReturn(outputFormat);

        final StringWriter writer = mock(StringWriter.class);
        whenNew(StringWriter.class).withNoArguments().thenReturn(writer);

        final XMLWriter xmlWriter = mock(XMLWriter.class);
        whenNew(XMLWriter.class).withArguments(writer, outputFormat).thenReturn(xmlWriter);

        mockStatic(FileUtils.class);

        final Document document = mock(Document.class);
        mockStatic(DocumentHelper.class);
        when(DocumentHelper.parseText(xml)).thenReturn(document);

        new XmlFormatter().formatAndWriteXml(xml, outputFile);

        verify(xmlWriter).write(document);

        verifyStatic(FileUtils.class);
        FileUtils.writeStringToFile(outputFile, writer.toString(), "utf-8");
    }

}
