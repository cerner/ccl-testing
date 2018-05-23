package com.cerner.ccltesting.maven.ccl.mojo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;

/**
 * Unit tests for {@link CompileMojo}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { BaseCclMojo.class, CclExecutor.class, CclResourceUploader.class, 
        CompileMojo.class })
public class CompileMojoTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Test the compilation of scripts and include files.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteNoDirectory() throws Exception {
        final File sourceDir = new File("target/unit/src/main/ccl");
        FileUtils.deleteDirectory(sourceDir);

        final CompileMojo mojo = new CompileMojo();
        mojo.sourceDirectory = sourceDir;

        expected.expect(MojoFailureException.class);
        expected.expectMessage("Source directory does not exist:");
        mojo.execute();
    }

    /**
     * Test setting of the {@code ccl-skipCompile} property.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipCompile() throws Exception {
        final CompileMojo mojo = new CompileMojo();
        final Log log = mock(Log.class);

        mojo.skipCompile = true;

        mojo.setLog(log);
        mojo.execute();

        verify(log, times(1)).info("Skipping compile goal");
    }
}
