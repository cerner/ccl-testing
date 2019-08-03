package com.cerner.ccl.testing.maven.ccl.reports;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Unit tests for {@link AbstractCCLMavenReport}.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractCCLMavenReportTest {
    /**
     * A {@link Rule} used to retrieve the current test name.
     */
    @Rule
    public TestName testName = new TestName();
    private final ConcreteReportMojo mojo = new ConcreteReportMojo();

    /**
     * Test the retrieval of an existent file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDirectoryFile() throws Exception {
        final File file = createFile();
        assertThat(AbstractCCLMavenReport.getDirectoryFile(file.getParentFile(), file.getName())).isEqualTo(file);
    }

    /**
     * If the given directory is not, in fact, a directory, then {@code null} should be returned.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDirectoryFileNotDirectory() throws Exception {
        assertThat(AbstractCCLMavenReport.getDirectoryFile(createFile(), "test.txt")).isNull();
    }

    /**
     * If the file does not exist, then {@code null} should be returned.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDirectoryFileNotExists() throws Exception {
        final File file = createFile();
        FileUtils.forceDelete(file);
        assertThat(file.exists()).isFalse();
        assertThat(AbstractCCLMavenReport.getDirectoryFile(file.getParentFile(), file.getName())).isNull();
    }

    /**
     * If the request file is not actually a file, then {@code null} should be returned.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDirectoryFileNotFile() throws Exception {
        final File file = createFile();
        final File parentFile = file.getParentFile();
        assertThat(AbstractCCLMavenReport.getDirectoryFile(parentFile.getParentFile(), parentFile.getName())).isNull();
    }

    /**
     * Test the retrieval of the output directory.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetOutputDirectory() throws Exception {
        final File file = createFile();
        mojo.outputDirectory = file;
        assertThat(mojo.getOutputDirectory()).isEqualTo(file.getAbsolutePath());
    }

    /**
     * Test the retrieval of the project.
     */
    @Test
    public void testGetProject() {
        final MavenProject project = mock(MavenProject.class);
        mojo.project = project;
        assertThat(mojo.getProject()).isEqualTo(project);
    }

    /**
     * Test the retrieval of the site renderer.
     */
    @Test
    public void testGetSiteRenderer() {
        final Renderer renderer = mock(Renderer.class);
        mojo.siteRenderer = renderer;
        assertThat(mojo.getSiteRenderer()).isEqualTo(renderer);
    }

    /**
     * Create a file.
     *
     * @return A {@link File} representing a file.
     * @throws IOException
     *             If any errors occur while creating the file.
     */
    private File createFile() throws IOException {
        return File.createTempFile(testName.getMethodName(), null);
    }

    /**
     * A concrete implementation of {@link AbstractCCLMavenReport} to be used for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteReportMojo extends AbstractCCLMavenReport {

        public ConcreteReportMojo() {
        }

        @Override
        public String getOutputName() {
            return null;
        }

        @Override
        public String getName(final Locale locale) {
            return null;
        }

        @Override
        public String getDescription(final Locale locale) {
            return null;
        }

        @Override
        protected void executeReport(final Locale locale) {
            // no-op
        }

    }
}
