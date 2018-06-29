package com.cerner.ccl.analysis.mojo;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;

import io.takari.maven.testing.TestMavenRuntime;
import io.takari.maven.testing.TestResources;

/**
 * Integration Tests for WhitenoiseMojo.
 *
 * @author Fred Eckertson
 *
 */
// TODO - finish this test case.
// NOTE: in order for these integration tests to work the "domain profile" must be activated when the "maven-profile" 
// property equals its profileId and it must set the "maven-profile" property equal to its id.
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
        System.out.println("********* the profileId is " + System.getProperty("maven-profile") + " *********");
        System.setProperty("doCompile", "true");
        maven.executeMojo(basedir, "whitenoise-report");
        System.out.println("done");

        MavenSession session = maven.newMavenSession(project);
        WhitenoiseMojo mojo = (WhitenoiseMojo) maven.lookupConfiguredMojo(session,
                maven.newMojoExecution("whitenoise-report"));
        assertThat(mojo.hostCredentialsId).isEqualTo("spoon");
    }
}
