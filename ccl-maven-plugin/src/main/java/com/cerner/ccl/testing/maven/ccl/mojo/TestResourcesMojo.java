package com.cerner.ccl.testing.maven.ccl.mojo;

import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * A mojo to upload resources used by test programs.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 *
 */
@Mojo(name = "process-test-resources")
public class TestResourcesMojo extends BaseCclResourceMojo {
    /**
     * The list of project test resources
     *
     */
    @Parameter(defaultValue = "${project.testResources}", required = true, readonly = true)
    List<Resource> testResources;

    /**
     * When set to {@code true}, causes this mojo to skip processing.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipProcessTestResources", defaultValue = "${ccl-skipProcessing}")
    protected boolean skipProcessTestResources;

    public void execute() throws MojoExecutionException {
        if (skipProcessTestResources) {
            getLog().info("Skipping process-test-resources goal");
            return;
        }

        if (!testResources.isEmpty())
            upload(testResources);
        else
            getLog().info("No test resources to process");
    }
}
