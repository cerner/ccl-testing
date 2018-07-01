package com.cerner.ccl.testing.maven.ccl.mojo;

import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * A mojo to upload resources.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */
@Mojo(name = "process-resources")
public class ResourcesMojo extends BaseCclResourceMojo {
    /**
     * The list of project resources
     *
     */
    @Parameter(defaultValue = "${project.resources}", required = true, readonly = true)
    protected List<Resource> resources;

    /**
     * When set to {@code true}, causes this mojo to skip processing.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipProcessResources", defaultValue = "${ccl-skipProcessing}")
    protected boolean skipProcessResources;

    public void execute() throws MojoExecutionException {
        if (skipProcessResources) {
            getLog().info("Skipping process-resources goal");
            return;
        }

        if (!resources.isEmpty())
            upload(resources);
        else
            getLog().info("No resources to process");
    }
}
