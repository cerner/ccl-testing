package com.cerner.ccl.analysis.core;

import java.io.IOException;
import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * An abstraction providing common methods for tests that deal with XML documents.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractJDomTest extends AbstractFileReaderTest {
    /**
     * Read a file and convert it to a JDom document.
     *
     * @param filename
     *            The name of the file to be read.
     * @return A {@link Document} representing the desired XML document.
     * @throws IOException
     *             If any errors occur during the read-in of the file.
     * @throws JDOMException
     *             If any errors occur during the parsing of the XML document.
     * @see #toString(String)
     */
    protected Document toDocument(final String filename) throws IOException, JDOMException {
        return new SAXBuilder().build(new StringReader(toString(filename)));
    }
}
