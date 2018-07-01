package com.cerner.ccl.testing.maven.ccl.mojo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import com.cerner.ccl.testing.maven.ccl.mojo.TestResourcesMojo;

/**
 * Unit tests of {@link TestResourcesMojo}.
 *
 * @author Mark Cummings
 */

public class TestResourcesMojoTest {
    /**
     * Test setting of the {@code ccl-skipProcessTestResources} property.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipProcessTestResources() throws Exception {
        final TestResourcesMojo mojo = new TestResourcesMojo();
        final Log log = mock(Log.class);

        mojo.skipProcessTestResources = true;

        mojo.setLog(log);
        mojo.execute();

        verify(log, times(1)).info("Skipping process-test-resources goal");
    }
}