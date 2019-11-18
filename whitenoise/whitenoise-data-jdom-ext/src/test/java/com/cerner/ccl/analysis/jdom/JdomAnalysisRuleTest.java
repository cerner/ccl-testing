package com.cerner.ccl.analysis.jdom;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.discovery.tools.Service;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.jdom.JdomAnalysisRule.Delegate;

/**
 * Unit tests for {@link JdomAnalysisRule}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { JdomAnalysisRule.class, SAXBuilder.class, Service.class, StringReader.class })
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class JdomAnalysisRuleTest {

    /**
     *
     * A JdomAnalysisDelegeate to be used for testing JdomAnalysisRule.Delegate methods based on translate1.xml.
     *
     * @author Fred Eckertson
     *
     */
    private class MyDelegate extends JdomAnalysisRule.Delegate {

        public MyDelegate(final Document document) {
            super(document);
        }

        @Override
        public Set<Violation> getCheckedViolations() {
            return null;
        }

        @Override
        protected Set<Violation> analyze() {
            return null;
        }
    }

    /**
     * Test the execution of delegate analyzers.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyze() throws Exception {
        final String prgXml = "i am CCL xml";

        final StringReader stringReader = mock(StringReader.class);
        whenNew(StringReader.class).withArguments(prgXml).thenReturn(stringReader);

        final SAXBuilder saxBuilder = mock(SAXBuilder.class);
        whenNew(SAXBuilder.class).withNoArguments().thenReturn(saxBuilder);

        final Document document = mock(Document.class);
        when(saxBuilder.build(stringReader)).thenReturn(document);

        final Violation violation = mock(Violation.class);
        final Delegate delegate = mock(Delegate.class);
        when(delegate.analyze()).thenReturn(Collections.singleton(violation));

        @SuppressWarnings("unchecked")
        final Enumeration<Delegate> delegates = mock(Enumeration.class);
        when(delegates.hasMoreElements()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(delegates.nextElement()).thenReturn(delegate);

        mockStatic(Service.class);
        when(Service.providers(ArgumentMatchers.<SPInterface> any(), ArgumentMatchers.<ClassLoaders> any()))
                .thenReturn(delegates);

        Set<Violation> violations = new JdomAnalysisRule().analyze(prgXml);
        assertThat(violations).containsOnly(violation);
    }

    /**
     * Confirms that getCallGraph and getInverseCallGraph work as expected and that they are singleton calls.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testGetNameCallGraph() throws Exception {
        String xml = loadResource("/xml/translate1.xml");
        Document document = new SAXBuilder().build(new StringReader(xml));

        MyDelegate myDelegate = new MyDelegate(document);
        Map<String, Set<String>> inverseCallGraph = myDelegate.getInverseNameCallGraph();
        assertThat(inverseCallGraph).isEqualTo(myDelegate.getInverseNameCallGraph());
        Map<String, Set<String>> nameCallGraph = myDelegate.getNameCallGraph();

        assertThat(nameCallGraph.keySet()).containsOnly("ZC_PROGRAM.", "SUB0", "SUB1", "SUB2", "PUBLIC::SUB3",
                "PUBLIC::SUB4", "SUB5", "PUBLIC::SUB6");
        assertThat(nameCallGraph.get("ZC_PROGRAM.")).containsOnly("SUB2", "SUB3", "SUB4", "PUBLIC::SUB1",
                "NS0000::SUB1", "NS0000::SUB2");
        assertThat(nameCallGraph.get("SUB0")).isEmpty();
        assertThat(nameCallGraph.get("SUB1")).isEmpty();
        assertThat(nameCallGraph.get("SUB2")).containsOnly("SUB1");
        assertThat(nameCallGraph.get("PUBLIC::SUB3")).containsOnly("SUB1", "SUB2");
        assertThat(nameCallGraph.get("PUBLIC::SUB4")).containsOnly("SUB1", "SUB2");
        assertThat(nameCallGraph.get("SUB5")).containsOnly("SUB2");
        assertThat(nameCallGraph.get("PUBLIC::SUB6")).containsOnly("SUB5");

        assertThat(inverseCallGraph.keySet()).containsOnly("SUB1", "SUB2", "SUB3", "SUB4", "SUB5", "PUBLIC::SUB1",
                "NS0000::SUB1", "NS0000::SUB2");
        assertThat(inverseCallGraph.get("SUB1")).containsOnly("SUB2", "PUBLIC::SUB3", "PUBLIC::SUB4");
        assertThat(inverseCallGraph.get("SUB2")).containsOnly("ZC_PROGRAM.", "PUBLIC::SUB3", "PUBLIC::SUB4", "SUB5");
        assertThat(inverseCallGraph.get("SUB3")).containsOnly("ZC_PROGRAM.");
        assertThat(inverseCallGraph.get("SUB4")).containsOnly("ZC_PROGRAM.");
        assertThat(inverseCallGraph.get("SUB5")).containsOnly("PUBLIC::SUB6");
        assertThat(inverseCallGraph.get("PUBLIC::SUB1")).containsOnly("ZC_PROGRAM.");
        assertThat(inverseCallGraph.get("NS0000::SUB1")).containsOnly("ZC_PROGRAM.");
        assertThat(inverseCallGraph.get("NS0000::SUB2")).containsOnly("ZC_PROGRAM.");
    }

    /**
     * Confirms that getCallGraph and getInverseCallGraph work as expected when there are function calls wrapped in
     * various types of other statements such as if, case and while.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testGetNameCallGraph2() throws Exception {
        String xml = loadResource("/xml/translate2.xml");
        Document document = new SAXBuilder().build(new StringReader(xml));
        MyDelegate myDelegate = new MyDelegate(document);
        Map<String, Set<String>> inverseCallGraph = myDelegate.getInverseNameCallGraph();
        assertThat(inverseCallGraph).isEqualTo(myDelegate.getInverseNameCallGraph());
        Map<String, Set<String>> nameCallGraph = myDelegate.getNameCallGraph();

        assertThat(nameCallGraph.keySet()).containsOnly("ZC_PROGRAM.", "SUB0", "SUB1", "SUB2", "SUB3", "SUB4", "SUB5",
                "SUB6", "SUB7", "SUB8");
        assertThat(nameCallGraph.get("ZC_PROGRAM.")).containsOnly("ECHO", "SUB0", "SUB1", "SUB2", "SUB3", "SUB4",
                "SUB5", "SUB6");
        assertThat(nameCallGraph.get("SUB0")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB1")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB2")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB3")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB4")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB5")).containsOnly("ECHO", "BUILD2", "SUB8");
        assertThat(nameCallGraph.get("SUB6")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB7")).containsOnly("ECHO", "BUILD2");
        assertThat(nameCallGraph.get("SUB8")).containsOnly("ECHO", "BUILD2", "SUB7");
    }

    /**
     * Performs cursory checks of the getCallGraph method.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testGetCallGraph() throws Exception {
        String xml = loadResource("/xml/translate1.xml");
        Document document = new SAXBuilder().build(new StringReader(xml));

        MyDelegate myDelegate = new MyDelegate(document);
        Map<Element, Set<Element>> callGraph = myDelegate.getCallGraph();

        assertThat(callGraph.size()).isEqualTo(8);
    }

    /**
     * Performs cursory checks of the getCallGraph method when there are subroutine calls wrapped in various types of
     * other statements such as if, case and while.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testGetCallGraph2() throws Exception {
        String xml = loadResource("/xml/translate2.xml");
        Document document = new SAXBuilder().build(new StringReader(xml));

        MyDelegate myDelegate = new MyDelegate(document);
        Map<Element, Set<Element>> callGraph = myDelegate.getCallGraph();

        assertThat(callGraph.size()).isEqualTo(10);
    }

    private String loadResource(final String resourceName) throws IOException {
        final InputStream is = JdomAnalysisRuleTest.class.getResourceAsStream(resourceName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        final StringBuilder sbData = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sbData.append(line).append("\n");
        }
        return sbData.toString();
    }
}
