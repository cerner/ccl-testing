package com.cerner.ccltesting.maven.ccl.mojo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

/**
 * Unit tests of {@link ResourcesMojo}.
 *
 * @author Mark Cummings
 */

public class ResourcesMojoTest {
    /**
     * Test setting of the {@code ccl-skipProcessResources} property.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipProcessResources() throws Exception {
        final ResourcesMojo mojo = new ResourcesMojo();
        final Log log = mock(Log.class);

        mojo.skipProcessResources = true;

        mojo.setLog(log);
        mojo.execute();

        verify(log, times(1)).info("Skipping process-resources goal");
    }
}