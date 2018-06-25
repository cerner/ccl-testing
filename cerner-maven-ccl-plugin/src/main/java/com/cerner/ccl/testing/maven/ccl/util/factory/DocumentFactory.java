package com.cerner.ccl.testing.maven.ccl.util.factory;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A factory to create {@link Document} objects.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentFactory {
    private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    /**
     * Create a document object containing the given XML.
     *
     * @param xml
     *            The XML to be made into a document data object.
     * @return A {@link Document} object representing the given XML.
     */
    public static Document create(final String xml) {
        try {
            final DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (final SAXException e) {
            throw new RuntimeException("Failed to parse XML data: " + xml, e);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read XML data: " + xml, e);
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to create document builder.", e);
        }
    }
}
