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
 * Unit Tests for WhitenoiseMojo.
 *
 * @author Fred Eckertson
 *
 */
// TODO - use this or get rid of it.
// TODO - finish this test case.
//@RunWith(PowerMockRunner.class)
//@PrepareForTest(value = { WhitenoiseMojo.class, FileUtils.class, BackendNodePasswordCredential.class,
//        BackendNodePrincipal.class, CclExecutor.class, MillenniumDomainPrincipal.class,
//        MillenniumDomainPasswordCredential.class })
public class WhitenoiseMojoTest {


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
        // TODo - use integration testing for tests that require back-end interactions. mock then in unit tests.
        // TODO - figure out how to read this from the current profile (or maybe it can set the profile ?).
        System.setProperty("maven-profile", "provide");
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
