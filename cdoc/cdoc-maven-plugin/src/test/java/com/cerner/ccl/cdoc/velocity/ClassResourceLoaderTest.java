package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.junit.Test;

/**
 * Unit tests for {@link ClassResourceLoader}.
 *
 * @author Joshua Hyde
 *
 */
public class ClassResourceLoaderTest {
    private final ClassResourceLoader loader = new ClassResourceLoader();

    /**
     * Test the retrieval of a resource as a stream.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetResourceStream() throws Exception {
        try (final InputStream stream = loader.getResourceStream("ClassResourceLoader-testGetResourceStream.txt")) {
            assertThat(IOUtils.toString(stream, "utf-8")).isEqualTo("test");
        }
    }

    /**
     * Looking up a non-existent resource should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetResourceStreamNotFound() throws Exception {
        final String source = "i will never ever exist!";
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> {
            loader.getResourceStream(source);
        });
        assertThat(e.getMessage()).isEqualTo("Resource not found: " + source);
    }

    /**
     * Since resources on the classpath aren't expected to be modified at runtime, this should always return
     * {@code false}.
     */
    @Test
    public void testIsSourceModified() {
        assertThat(loader.isSourceModified(mock(Resource.class))).isFalse();
    }

    /**
     * The "last modified" of a classpath resource is not available.
     */
    @Test
    public void testGetLastModified() {
        assertThat(loader.getLastModified(mock(Resource.class))).isZero();
    }
}
