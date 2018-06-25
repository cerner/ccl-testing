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
        System.out.println("********* the profileId is " + System.getProperty("profileId") + " *********");
        File basedir = testResources.getBasedir("testIt");
        // NOTE: the "domain profile" must be listed first 
        // and it must be activated when the "maven-profile" property equals its profileId.
        System.setProperty("doCompile", "true");
        maven.executeMojo(basedir, "whitenoise-report");
        System.out.println("done");

        MavenProject project = maven.readMavenProject(basedir);
        MavenSession session = maven.newMavenSession(project);
        WhitenoiseMojo mojo = (WhitenoiseMojo) maven.lookupConfiguredMojo(session,
                maven.newMojoExecution("whitenoise-report"));
        assertThat(mojo.hostCredentialsId).isEqualTo("spoon");
    }
}
