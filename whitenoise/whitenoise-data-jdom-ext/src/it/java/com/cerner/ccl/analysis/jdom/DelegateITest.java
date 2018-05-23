package com.cerner.ccl.analysis.jdom;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.jdom.JdomAnalysisRule.Delegate;

/**
 * Integration tests for {@link JdomAnalysisRule.Delegate}.
 *
 * @author Joshua Hyde
 *
 */

public class DelegateITest {
    /**
     * Test the parsing of the line number from the {@code loc} attribute.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetLineNumber() throws Exception {
        Document document = toDocument("<TEST loc=\"12.12\" />");
        assertThat(new ConcreteDelegate(document).getLineNumber(document.getRootElement())).isEqualTo(12);
    }

    /**
     * If the element has no {@code loc} attribute, then {@code null} should be returned for the line number.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetLineNumberNoLocAttribute() throws Exception {
        Document document = toDocument("<TEST />");
        assertThat(new ConcreteDelegate(document).getLineNumber(document.getRootElement())).isNull();
    }

    /**
     * If the script wasn't compiled in debug mode, then the location will be "0.0"; in such a case, {@code null} should be returned for the line number.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetLineNumberZeroValue() throws Exception {
        Document document = toDocument("<TEST loc=\"0.0\" />");
        assertThat(new ConcreteDelegate(document).getLineNumber(document.getRootElement())).isNull();
    }

    /**
     * Convert a string of XML to a document.
     *
     * @param xml
     *            The XML to be converted.
     * @return A {@link Document} representing the given XML.
     * @throws IOException
     *             If any errors occur during the read-in of the XML.
     * @throws JDOMException
     *             If any errors occur during the parsing of the XML.
     */
    private Document toDocument(final String xml) throws IOException, JDOMException {
        return new SAXBuilder().build(new StringReader(xml));
    }

    /**
     * A concrete {@link Delegate} implementation used for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteDelegate extends Delegate {
        public ConcreteDelegate(Document document) {
            super(document);
        }

        @Override
        protected Set<Violation> analyze() {
            return null;
        }

        @Override
        public Set<Violation> getCheckedViolations() {
        	return null;
        }
    }
}
