package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.StructureBuilder;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;

/**
 * A mojo to validate certain facts about the environment against which the plugin is to run.
 *
 * @author Joshua Hyde
 * @since 1.1
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.VALIDATE)
public class ValidateMojo extends BaseCclMojo {
    /**
     * The rule that dictates the required state of the environment. An example configuration is:
     *
     * <pre>
     * &lt;configuration&gt;
     *   &lt;validationRule&gt;
     *     &lt;testFrameworkVersion&gt;[1.0,2.0)&lt;/testFrameworkVersion&gt;
     *   &lt;/validationRule&gt;
     * &lt;/configuration&gt;
     * </pre>
     *
     */
    @Parameter(required = true)
    protected ValidationRule validationRule;

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        validateTestFrameworkVersion();
    }

    /**
     * Validate the CCL testing framework version.
     *
     * @throws MojoExecutionException
     *             If any errors occur while retrieving the test framework data.
     * @throws MojoFailureException
     *             If the range specified within the validation rule does not contain the version returned by the CCL
     *             script.
     */
    private void validateTestFrameworkVersion() throws MojoExecutionException, MojoFailureException {
        if (skipProcessing) {
            getLog().info("Validation is skipped.");
            return;
        }

        final String testFrameworkVersion = validationRule.getTestFrameworkVersion();
        if (testFrameworkVersion == null)
            return;

        final VersionRange range = getVersionRange(testFrameworkVersion);

        final Record stateReply = RecordFactory.create("stateReply",
                StructureBuilder.getBuilder().addVC("state").build());
        Subject.doAs(getSubject(), new PrivilegedAction<Void>() {
            public Void run() {
                CclExecutor executor;
                try {
                    executor = createCclExecutor();
                    executor.addScriptExecution("cclut_get_framework_state").withReplace("reply", stateReply).commit();
                    executor.execute();
                } catch (MojoExecutionException | MojoFailureException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        final String actualVersion = parseRetrievedVersion(stateReply.getVC("state"));
        final ArtifactVersion frameworkVersion = new DefaultArtifactVersion(actualVersion);
        if (!range.containsVersion(frameworkVersion)) {
            throw new MojoFailureException("Unexpected CCL testing framework version: version " + actualVersion
                    + " is not in the range " + testFrameworkVersion);
        }
    }

    /**
     * Parse the version range given by the user.
     *
     * @param versionRange
     *            The version range to be parsed.
     * @return A {@link VersionRange} object representing the given version range.
     * @throws MojoExecutionException
     *             If any errors occur while parsing the version range.
     */
    private VersionRange getVersionRange(final String versionRange) throws MojoExecutionException {
        try {
            return VersionRange.createFromVersionSpec(versionRange);
        } catch (final InvalidVersionSpecificationException e) {
            throw new MojoExecutionException("Failed to parse supplied version range: " + versionRange, e);
        }
    }

    /**
     * Parse the version from the given state text.
     *
     * @param stateText
     *            The text returned by the CCL testing framework indicating the current version.
     * @return The version stated within the reply.
     * @throws MojoExecutionException
     *             If any errors occur during the reading of the data.
     */
    private String parseRetrievedVersion(final String stateText) throws MojoExecutionException {
        Document document = null;
        try {
            document = new SAXBuilder().build(new ByteArrayInputStream(stateText.getBytes("utf-8")));
        } catch (final JDOMException e) {
            throw new MojoExecutionException("Failed to parse state text: [" + stateText + "]", e);
        } catch (final IOException e) {
            throw new MojoExecutionException("Error reading data from state text.", e);
        }
        return document.getRootElement().getChildText("VERSION");
    }

}
