package com.cerner.ccltesting.maven.ccl.mojo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

/**
 * Unit tests for {@link TestCompileMojo}.
 *
 * @author Mark Cummings
 *
 */

public class TestCompileMojoTest {
    /**
     * Test setting of the {@code ccl-SkipTestCompile} property.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipTestCompile() throws Exception {
        final TestCompileMojo mojo = new TestCompileMojo();
        final Log log = mock(Log.class);

        mojo.skipTestCompile = true;

        mojo.setLog(log);
        mojo.execute();

        verify(log, times(1)).info("Skipping test-compile goal");
    }
}
