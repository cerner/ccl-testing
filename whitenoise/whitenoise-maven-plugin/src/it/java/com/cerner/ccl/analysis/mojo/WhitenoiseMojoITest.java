package com.cerner.ccl.analysis.mojo;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;

/**
 * Integration Tests for WhitenoiseMojo.
 * <br />
 * NOTE: in order for these integration tests to work the "domain profile" must be activated when the "maven-profile"
 * property equals its profile id and it must set the "maven-profile" property equal to its id.
 *
 * @author Fred Eckertson
 *
 */
// TODO - Improve this integration testing.
public class WhitenoiseMojoITest {
    /**
     * Access to the test resources.
     */
    @Rule
    public final TestResources testResources = new TestResources();

    /**
     * Access to the maven runtime.
     */
    @Rule
    public final TestMavenRuntime maven = new TestMavenRuntime();

    /**
     * Test the mojo.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testIt() throws Exception {
        File basedir = testResources.getBasedir("testIt");
        MavenProject project = maven.readMavenProject(basedir);

        System.setProperty("doCompile", "true");
        maven.executeMojo(basedir, "whitenoise-report");

        MavenSession session = maven.newMavenSession(project);
        WhitenoiseMojo mojo = (WhitenoiseMojo) maven.lookupConfiguredMojo(session, maven.newMojoExecution("whitenoise-report"));
    }

    /**
     * Test that the mojo correctly catches unused variable violations w.r.t. subroutine transcendence and
     * subroutine calls withing report writer sections.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testVariableScoping() throws Exception {
        File basedir = testResources.getBasedir("testVariableScoping");
        MavenProject project = maven.readMavenProject(basedir);
        System.setProperty("doCompile", "true");
        System.setProperty("outputRawData", "true");
        maven.executeMojo(basedir, "whitenoise-report");
        File dataFile = FileUtils.getFile(new File(project.getBuild().getDirectory()), "whitenoise", "violations.txt");
        String data = FileUtils.readFileToString(dataFile, Charset.forName("utf-8")).trim();
        assertThat(data).isEqualTo("<sample_script,CORE.UNUSED_VARIABLE_DECLARATION,VAR2 is declared but never used,12>");
    }
}
