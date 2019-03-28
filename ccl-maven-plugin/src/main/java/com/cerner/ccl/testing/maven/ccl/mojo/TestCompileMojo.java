package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.File;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.cerner.ccl.j4ccl.CclExecutor;

/**
 * A mojo used to compile CCL testing code.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 *
 */
@Mojo(name = "test-compile")
public class TestCompileMojo extends BaseCclCompilationMojo {

    /**
     * The location within the project directory where the test CCL program file(s) can be found.
     *
     */
    @Parameter(property = "ccl-testSourceDirectory", defaultValue = "src/test/ccl")
    protected File cclTestSourceDirectory;

    /**
     * When set to {@code true}, causes this mojo to skip processing.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipTestCompile", defaultValue = "${ccl-skipProcessing}")
    protected boolean skipTestCompile;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTestCompile) {
            getLog().info("Skipping test-compile goal");
            return;
        }

        // TODO: Add integration tests that fail if not done this way.
        Subject subject = getSubject();
        final CclExecutor executor = createCclExecutor();
        queueIncludeCompilation(executor, cclTestSourceDirectory);
        queueScriptCompilation(executor, cclTestSourceDirectory);
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                executor.execute();
                return null;
            }
        });
    }
}
