package com.cerner.ccl.testing.xsl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cerner.ccl.testing.xsl.StringURIResolver;
import com.cerner.ccl.testing.xsl.XslAPI;

/**
 * Unit tests for {@link XslAPITest}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DocumentBuilderFactory.class, IOUtils.class, StreamResult.class, StreamSource.class, StringReader.class, StringURIResolver.class, TransformerFactory.class,
        XPathFactory.class, XslAPI.class })
public class XslAPITest {
    /**
     * Test the transformation of XML to XSL.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testTransform() throws Exception {
        final String xml = "i am xml";
        final String xsl = "i am xsl";
        final String xmlResultString = "i am an XML result";
        final URIResolver resolver = mock(URIResolver.class);

        final StreamSource xslSource = mock(StreamSource.class);
        final StringReader xslReader = mock(StringReader.class);
        whenNew(StringReader.class).withArguments(xsl).thenReturn(xslReader);
        whenNew(StreamSource.class).withArguments(xslReader).thenReturn(xslSource);

        final StringWriter xmlWriter = mock(StringWriter.class);
        final StreamResult xmlResult = mock(StreamResult.class);
        final StreamSource xmlSource = mock(StreamSource.class);
        final StringReader xmlReader = mock(StringReader.class);
        whenNew(StringReader.class).withArguments(xml).thenReturn(xmlReader);
        whenNew(StreamSource.class).withArguments(xmlReader).thenReturn(xmlSource);

        whenNew(StringWriter.class).withNoArguments().thenReturn(xmlWriter);
        whenNew(StreamResult.class).withArguments(xmlWriter).thenReturn(xmlResult);

        when(xmlWriter.getBuffer()).thenReturn(new StringBuffer(xmlResultString));

        final Transformer transformer = mock(Transformer.class);
        final TransformerFactory transformerFactory = mock(TransformerFactory.class);
        mockStatic(TransformerFactory.class);
        when(TransformerFactory.newInstance()).thenReturn(transformerFactory);
        when(transformerFactory.newTransformer(xslSource)).thenReturn(transformer);

        assertThat(XslAPI.transform(xml, xsl, resolver)).isEqualTo(xmlResultString);
        verify(transformer).setURIResolver(resolver);
        verify(transformer).transform(xmlSource, xmlResult);
    }

    /**
     * Test the retrieval of an XPath node list.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetXPathNodeList() throws Exception {
        final String expressionText = "i am an expression";
        final XPathExpression expression = setUpXpathExpression(expressionText);
        final Node node = mock(Node.class);
        final NodeList nodeList = mock(NodeList.class);
        when(expression.evaluate(node, XPathConstants.NODESET)).thenReturn(nodeList);
        assertThat(XslAPI.getXPathNodeList(node, expressionText)).isEqualTo(nodeList);
    }

    /**
     * Test the retrieval of an XPath node list.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetXPathNodeListNotFound() throws Exception {
        final String expressionText = "i am an expression";
        final XPathExpression expression = setUpXpathExpression(expressionText);
        final Node node = mock(Node.class);
        when(expression.evaluate(node, XPathConstants.NODESET)).thenThrow(new XPathExpressionException("blech"));
        assertThat(XslAPI.getXPathNodeList(node, expressionText)).isNull();
    }

    /**
     * Test the evaluation of a string textual value.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetNodeXPathValue() throws Exception {
        final String expressionText = "i am an expression";
        final XPathExpression expression = setUpXpathExpression(expressionText);
        final Node node = mock(Node.class);
        final String evaluatedText = "i am the evaluated text";
        when(expression.evaluate(node, XPathConstants.STRING)).thenReturn(evaluatedText);
        assertThat(XslAPI.getNodeXPathValue(node, expressionText)).isEqualTo(evaluatedText);
    }

    /**
     * Test that, if the xpath does not resolve to anything, that a blank string is returned.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetNodeXPathValueNotFound() throws Exception {
        final String expressionText = "i am an expression";
        final XPathExpression expression = setUpXpathExpression(expressionText);
        final Node node = mock(Node.class);
        when(expression.evaluate(node, XPathConstants.STRING)).thenThrow(new XPathExpressionException("kablooey!"));
        assertThat(XslAPI.getNodeXPathValue(node, expressionText)).isEmpty();
    }

    /**
     * Test the creation of a document from a string.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDocumentFromString() throws Exception {
        final String xml = "i am xml text";

        final DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);
        mockStatic(DocumentBuilderFactory.class);
        when(DocumentBuilderFactory.newInstance()).thenReturn(factory);

        final DocumentBuilder builder = mock(DocumentBuilder.class);
        when(factory.newDocumentBuilder()).thenReturn(builder);

        final ByteArrayInputStream xmlStream = mock(ByteArrayInputStream.class);
        mockStatic(IOUtils.class);
        when(IOUtils.toInputStream(xml, "utf-8")).thenReturn(xmlStream);

        final Document document = mock(Document.class);
        when(builder.parse(xmlStream)).thenReturn(document);

        assertThat(XslAPI.getDocumentFromString(xml)).isEqualTo(document);
    }

    /**
     * Verify the creation of a new URI resolver.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetNewResolver() throws Exception {
        final StringURIResolver resolver = mock(StringURIResolver.class);
        whenNew(StringURIResolver.class).withNoArguments().thenReturn(resolver);
        assertThat(XslAPI.getNewResolver()).isEqualTo(resolver);
    }

    /**
     * Set up the compilation of an xpath expression.
     * 
     * @param expressionText
     *            The text of the xpath expression to be compiled.
     * @return An {@link XPathExpression} that will be treated as the compile xpath expression.
     * @throws XPathExpressionException
     *             If any errors occur during the mock setup.
     */
    private XPathExpression setUpXpathExpression(String expressionText) throws XPathExpressionException {
        final XPathExpression expression = mock(XPathExpression.class);
        final XPath xpath = setUpXpathGeneration();
        when(xpath.compile(expressionText)).thenReturn(expression);
        return expression;
    }

    /**
     * Set up an xpath object to be created by a factory.
     * 
     * @return An {@link XPath} object to be returned by any invocations of {@link XPathFactory#newXPath()}.
     */
    private XPath setUpXpathGeneration() {
        final XPathFactory factory = mock(XPathFactory.class);
        mockStatic(XPathFactory.class);
        when(XPathFactory.newInstance()).thenReturn(factory);

        final XPath xPath = mock(XPath.class);
        when(factory.newXPath()).thenReturn(xPath);
        return xPath;
    }
}
