package com.cerner.ccl.cdoc.velocity;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.Writer;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.cdoc.AbstractUnitTest;
import com.cerner.ccl.cdoc.mojo.data.Documentation;

/**
 * Unit tests for {@link SummaryGenerator}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { SummaryGenerator.class, VelocityContext.class })
public class SummaryGeneratorTest extends AbstractUnitTest {
    private SummaryGenerator generator;
    @Mock
    private VelocityEngine engine;
    @Mock
    private MavenProject project;
    @Mock
    private List<Documentation> documentation;
    @Mock
    private File cssDirectory;
    @Mock
    private Writer writer;

    /**
     * Set up the generator for each test.
     */
    @Before
    public void setUp() {
        generator = new InjectableGenerator(engine);
    }

    /**
     * Test the generation of the summary.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerate() throws Exception {
        final String projectName = "a-project-name";
        final String projectVersion = "1.374783937467-beta-1";
        when(project.getName()).thenReturn(projectName);
        when(project.getVersion()).thenReturn(projectVersion);

        final File tempCssDirectory = File.createTempFile("testGenerate", null).getParentFile();

        final VelocityContext context = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(context);

        final Template template = mock(Template.class);
        when(engine.getTemplate("/velocity/script-doc-summary.vm", "utf-8")).thenReturn(template);

        generator.generate(project, documentation, tempCssDirectory, writer);

        verify(context).put("projectName", projectName);
        verify(context).put("projectVersion", projectVersion);
        verify(context).put("docs", documentation);
        verify(context).put("cssDirectory", tempCssDirectory.toURI().toURL().toExternalForm());

        verify(template).merge(context, writer);
    }

    /**
     * Generation with a {@code null} CSS directory should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerateNullCssDirectory() throws Exception {
        expect(IllegalArgumentException.class);
        expect("CSS directory cannot be null.");
        generator.generate(project, documentation, null, writer);
    }

    /**
     * Generation with a {@code null} {@link List} of {@link Documentation} objects should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerateNullDocumentation() throws Exception {
        expect(IllegalArgumentException.class);
        expect("Documentation cannot be null.");
        generator.generate(project, null, cssDirectory, writer);
    }

    /**
     * Generation with a {@code null} {@link MavenProject} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerateNullProject() throws Exception {
        expect(IllegalArgumentException.class);
        expect("Maven project cannot be null.");
        generator.generate(null, documentation, cssDirectory, writer);
    }

    /**
     * Generation with a {@code null} {@link Writer} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerateNullWriter() throws Exception {
        expect(IllegalArgumentException.class);
        expect("Destination writer cannot be null.");
        generator.generate(project, documentation, cssDirectory, null);
    }

    /**
     * A mutation of {@link SummaryGenerator} that allows for the injection of the {@link VelocityEngine} used by the
     * generator.
     *
     * @author Joshua Hyde
     *
     */
    private static class InjectableGenerator extends SummaryGenerator {
        private final VelocityEngine engine;

        /**
         * Create a generator.
         *
         * @param engine
         *            The {@link VelocityEngine} to be used by this generator.
         */
        public InjectableGenerator(final VelocityEngine engine) {
            this.engine = engine;
        }

        @Override
        protected VelocityEngine getEngine() {
            return engine;
        }
    }
}
