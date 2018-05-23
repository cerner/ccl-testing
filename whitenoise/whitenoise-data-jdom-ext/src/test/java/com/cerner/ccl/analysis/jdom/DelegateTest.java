package com.cerner.ccl.analysis.jdom;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.jdom.JdomAnalysisRule.Delegate;

/**
 * Unit tests for {@link JdomAnalysisRule.Delegate}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { Delegate.class, XPathFactory.class, XPathExpression.class })
public class DelegateTest {
    private final Document mockDocument = mock(Document.class);
    private final Delegate delegate = new ConcreteDelegate(mockDocument);

    /**
     * Test the retrieval of the "name" element from within a CCL XML representation.
     */
    @Test
    public void testGetCclName() {
        final String cclName = "a name";
        final Element nameChild = mock(Element.class);
        when(nameChild.getAttributeValue("text")).thenReturn(cclName);

        final Element element = mock(Element.class);
        when(element.getChild("NAME")).thenReturn(nameChild);

        assertThat(delegate.getCclName(element)).isEqualTo(cclName);
    }

    /**
     * If the given element has no "NAME" child element, then a blank string should be all that's returned.
     */
    @Test
    public void testGetCclNameNoNameChild() {
        assertThat(delegate.getCclName(mock(Element.class))).isEmpty();
    }

    /**
     * Test the retrieval of defined exceptions.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDefinedSubroutines() throws Exception {
        final Document document = mock(Document.class);
        final Element firstSubroutine = mock(Element.class);
        final Element secondSubroutine = mock(Element.class);

        final Delegate toTest = new ConcreteDelegate(document) {
            @Override
            protected List<Element> selectNodesByName(final String name) {
                assertThat(name).isEqualTo("SUBROUTINE.");
                return Arrays.asList(firstSubroutine, secondSubroutine);
            }
        };

        final Collection<Element> definedSubroutines = toTest.getDefinedSubroutines();
        assertThat(definedSubroutines).contains(firstSubroutine);
        assertThat(definedSubroutines).contains(secondSubroutine);
        assertThat(definedSubroutines.size()).isEqualTo(2);

        // The list of defined subroutines should be cached.
        assertThat(definedSubroutines).isSameAs(toTest.getDefinedSubroutines());
    }

    /**
     * Test the retrieval of defined variable declarations.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetVariableDeclarations() throws Exception {
        final Document document = mock(Document.class);
        final Element firstDeclaration = mock(Element.class);
        final Element secondDeclaration = mock(Element.class);

        final Delegate toTest = new ConcreteDelegate(document) {
            @Override
            protected List<Element> selectNodesByName(final String name,
                    final String predicate) {
                assertThat(name).isEqualTo("Z_DECLARE.");
                assertThat(predicate).isEqualTo("[NAME and not(CALL.)]");
                return Arrays.asList(firstDeclaration, secondDeclaration);
            }
        };

        final List<Element> definedDeclarations = toTest.getVariableDeclarations();
        assertThat(definedDeclarations).containsExactly(firstDeclaration, secondDeclaration);
        // The list of defined variable declarations should be cached
        assertThat(definedDeclarations).isSameAs(toTest.getVariableDeclarations());
    }

    /**
     * Test the selection of attributes for a given document.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSelectAttributesDocument() throws Exception {
        final String expression = "xpath expression";

        final XPathFactory xpathFactory = mock(XPathFactory.class);
        final XPathExpression<Attribute> xpathExpression = mock(XPathExpression.class);
        final List<Attribute> attributes = mock(List.class);

        mockStatic(XPathFactory.class);

        when(XPathFactory.instance()).thenReturn(xpathFactory);
        when(xpathExpression.evaluate(mockDocument)).thenReturn(attributes);
        when(xpathFactory.compile(expression, Filters.attribute())).thenReturn(xpathExpression);

        assertThat(delegate.selectAttributes(expression)).isEqualTo(attributes);
    }

    /**
     * Test the selection of attributes for a given root element.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSelectAttributesElement() throws Exception {
        final String expression = "xpath expression";

        final Element rootElement = mock(Element.class);
        final XPathFactory xpathFactory = mock(XPathFactory.class);
        final XPathExpression<Attribute> xpathExpression = mock(XPathExpression.class);
        final List<Attribute> attributes = mock(List.class);

        mockStatic(XPathFactory.class);

        when(XPathFactory.instance()).thenReturn(xpathFactory);
        when(xpathExpression.evaluate(rootElement)).thenReturn(attributes);
        when(xpathFactory.compile(expression, Filters.attribute())).thenReturn(xpathExpression);

        assertThat(delegate.selectAttributes(rootElement, expression)).isEqualTo(attributes);
    }

    /**
     * Test the selection of nodes within a document.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSelectNodesDocument() throws Exception {
        final String expression = "xpath expression";

        final XPathFactory xpathFactory = mock(XPathFactory.class);
        final XPathExpression<Element> xpathExpression = mock(XPathExpression.class);
        final List<Element> elements = mock(List.class);

        mockStatic(XPathFactory.class);

        when(XPathFactory.instance()).thenReturn(xpathFactory);
        when(xpathExpression.evaluate(mockDocument)).thenReturn(elements);
        when(xpathFactory.compile(expression, Filters.element())).thenReturn(xpathExpression);

        assertThat(delegate.selectNodes(expression)).isEqualTo(elements);
    }

    /**
     * Test the selection of nodes from a given root element.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testSelectNodesElement() throws Exception {
        final String expression = "xpath expression";

        final Element rootElement = mock(Element.class);
        final XPathFactory xpathFactory = mock(XPathFactory.class);
        final XPathExpression<Element> xpathExpression = mock(XPathExpression.class);
        final List<Element> elements = mock(List.class);

        mockStatic(XPathFactory.class);

        when(XPathFactory.instance()).thenReturn(xpathFactory);
        when(xpathExpression.evaluate(rootElement)).thenReturn(elements);
        when(xpathFactory.compile(expression, Filters.element())).thenReturn(xpathExpression);

        assertThat(delegate.selectNodes(rootElement, expression)).isEqualTo(elements);
    }

    /**
     * A concrete implementation of {@link Delegate} for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteDelegate extends Delegate {
        public ConcreteDelegate(final Document document) {
            super(document);
        }

        @Override
        protected Set<Violation> analyze() {
            // no-op
            return null;
        }

        @Override
        public Set<Violation> getCheckedViolations() {
            // no-op
            return null;
        }
    }
}
