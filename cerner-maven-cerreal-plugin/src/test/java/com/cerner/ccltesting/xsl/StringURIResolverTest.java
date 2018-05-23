package com.cerner.ccltesting.xsl;

import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link StringURIResolver}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { StringURIResolver.class, StreamSource.class, StringReader.class })
public class StringURIResolverTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    private final StringURIResolver resolver = new StringURIResolver();

    /**
     * Test the resolution of a resource to a {@link Source}.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testResolve() throws Exception {
        final String resourceHref = "a.href";
        final String resourceBase = "a.base";
        final String resource = "a.resource";
        final StreamSource source = mock(StreamSource.class);
        final StringReader reader = mock(StringReader.class);
        whenNew(StringReader.class).withArguments(resource).thenReturn(reader);
        whenNew(StreamSource.class).withArguments(reader).thenReturn(source);

        resolver.addResource(resourceBase, resourceHref, resource);
        assertThat(resolver.resolve(resourceHref, resourceBase)).isEqualTo(source);
    }

    /**
     * If the resource hasn't been registered, then resolving should fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testResolveNotFound() throws Exception {
        final String resourceHref = "a.href";
        final String resourceBase = "a.base";
        expected.expect(TransformerException.class);
        expected.expectMessage("Failed to resolve [" + resourceBase + resourceHref + "] to a valid resource");
        resolver.resolve(resourceHref, resourceBase);
    }
}
