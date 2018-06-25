package com.cerner.ccl.analysis.mojo;

import java.io.File;

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
    }
}
