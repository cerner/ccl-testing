package com.cerner.ccl.parser.text;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.AbstractUnitTest;
import com.cerner.ccl.parser.data.IncludeDocumentation;
import com.cerner.ccl.parser.text.smoosh.DocumentationBlockIndexedSmoosher;

/**
 * Unit tests for {@link IncludeDocumentationFactory}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { IncludeDocumentation.class, IncludeDocumentationFactory.class })
public class IncludeDocumentationFactoryTest extends AbstractUnitTest {
    @Mock
    private DocumentationBlockIndexedSmoosher smoosher;
    private IncludeDocumentationFactory factory;

    /**
     * Set up the factory for each test.
     */
    @Before
    public void setUp() {
        factory = new IncludeDocumentationFactory(smoosher);
    }

    /**
     * Construction with a {@code null} smoosher should fail.
     */
    @Test
    public void testConstructNullSmoosher() {
        expect(IllegalArgumentException.class);
        expect("Smoosher cannot be null.");
        new IncludeDocumentationFactory(null);
    }

    /**
     * If the source fails the criteria for parsing into top-level documentation, it should indicate as much.
     */
    @Test
    public void testCannotParse() {
        final List<String> source = Arrays.asList("; blah blah", "/**", "doc", "*/");
        assertThat(factory.canParse(1, source)).isFalse();
    }

    /**
     * If the only lines above the documentation are definition directives, then parsing of the documentation should
     * still work.
     */
    @Test
    public void testCanParseWithDefinitions() {
        final List<String> source = Arrays.asList("%#ifndef TEST", "%#define TEST", "%#ifndef TEST2", "%#def TEST2",
                " ", "/**", "DOC", "*/");
        assertThat(factory.canParse(5, source)).isTrue();
    }

    /**
     * If the current line is the 0th line and it's the beginning of a documentation block, it should be parseable.
     */
    @Test
    public void testCanParseZeroLine() {
        final List<String> source = Arrays.asList("/**", "doc", "*/");
        assertThat(factory.canParse(0, source)).isTrue();
    }

    /**
     * The retrieval of the ending index should be delegated to the underlying smoosher.
     */
    @Test
    public void testGetEndingIndex() {
        when(smoosher.getEndingIndex()).thenReturn(Integer.valueOf(23));
        assertThat(smoosher.getEndingIndex()).isEqualTo(23);
    }

    /**
     * Test the production of an {@link IncludeDocumentation} object.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final List<String> source = Collections.singletonList("source");
        final int currentIndex = 4783;
        final String smooshed = "smooshed text";
        when(smoosher.smoosh(currentIndex + 1, source)).thenReturn(smooshed);

        final IncludeDocumentation doc = mock(IncludeDocumentation.class);
        whenNew(IncludeDocumentation.class).withArguments(smooshed).thenReturn(doc);

        assertThat(factory.parse(currentIndex, source)).isEqualTo(doc);
    }
}
