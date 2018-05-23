package com.cerner.ccltesting.xsl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A utility class used to apply XSL transformations from an XSL stylesheet to XML.
 * 
 * @author Jeff Wiedemann
 * 
 */

public class XslAPI {
    /**
     * Apply an XSL stylesheet to XML data.
     * 
     * @param xml
     *            The XML to be transformed.
     * @param xsl
     *            The XSL to be used as the transformation against the XML.
     * @param resolver
     *            A {@link URIResolver} used to resolve locations of resources.
     * @return The transformation result of the XSL application to the XML.
     * @throws XslAPIException
     *             If any errors occur during the transformation.
     */
    public static String transform(String xml, String xsl, URIResolver resolver) throws XslAPIException {
        // Use the static TransformerFactory.newInstance() method to instantiate
        // a TransformerFactory. The javax.xml.transform.TransformerFactory
        // system property setting determines the actual class to instantiate --
        // org.apache.xalan.transformer.TransformerImpl.
        final TransformerFactory tFactory = TransformerFactory.newInstance();

        // Initialize the transformation with the incoming XSL string
        final StreamSource xslSource = new StreamSource(new StringReader(xsl));
        Transformer transformer;

        try {
            transformer = tFactory.newTransformer(xslSource);
        } catch (TransformerConfigurationException e) {
            throw new XslAPIException("Failed to create a transformer factory.", e);
        }

        // This object allows document() and imports to be resolved at runtime
        transformer.setURIResolver(resolver);

        // Initialize the input XML reader with the incoming XML string
        final StreamSource xmlSource = new StreamSource(new StringReader(xml));

        // Initialize the output writer to store the resulting XML
        final StringWriter resultWriter = new StringWriter();
        final Result result = new StreamResult(resultWriter);

        try {
            transformer.transform(xmlSource, result);
        } catch (TransformerException e) {
            throw new XslAPIException("Failed to transform XML.", e);
        }

        return resultWriter.getBuffer().toString();
    }

    /**
     * Get a list of nodes matching an xpath expression.
     * 
     * @param node
     *            The parent {@link Node} on which the xpath expression is to be applied.
     * @param xPath
     *            The xpath expression to be applied.
     * @return {@code null} if nothing can be found using the given xpath expression; otherwise, a {@link NodeList} representing the results.
     */
    public static NodeList getXPathNodeList(Node node, String xPath) {
        try {
            return (NodeList) compileExpression(xPath).evaluate(node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Get the value of a node matching the given xpath expression.
     * 
     * @param node
     *            The parent {@link Node} on which the xpath expression is to be applied.
     * @param xPath
     *            The xpath expression to be applied.
     * @return A blank string if nothing can be found; otherwise, the value of the matching node.
     */
    public static String getNodeXPathValue(Node node, String xPath) {
        try {
            return (String) compileExpression(xPath).evaluate(node, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            return "";
        }
    }

    /**
     * Create a document from XML.
     * 
     * @param xml
     *            The XML to be used to create the document.
     * @return A {@link Document} of the given XML.
     * @throws XslAPIException
     *             If any errors occur during the document creation.
     */
    public static Document getDocumentFromString(String xml) throws XslAPIException {
        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        try {
            return domFactory.newDocumentBuilder().parse(IOUtils.toInputStream(xml, "utf-8"));
        } catch (ParserConfigurationException e) {
            throw new XslAPIException("Failed to build XML parser.", e);
        } catch (SAXException e) {
            throw new XslAPIException("Failed to parse XML.", e);
        } catch (IOException e) {
            throw new XslAPIException("Failed to read steam data.", e);
        }
    }

    /**
     * Create a new string URI resolver.
     * 
     * @return An instance of {@link StringURIResolver}.
     */
    public static StringURIResolver getNewResolver() {
        return new StringURIResolver();
    }

    /**
     * Compile an xpath expression.
     * 
     * @param xpathExpression
     *            The xpath expression to be compiled.
     * @return An {@link XPathExpression} representing the compilation of the given expression.
     * @throws IllegalArgumentException
     *             If the given argument cannot be compiled.
     */
    private static XPathExpression compileExpression(String xpathExpression) {
        try {
            return XPathFactory.newInstance().newXPath().compile(xpathExpression);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Unable to compile expression: " + xpathExpression, e);
        }
    }
}
