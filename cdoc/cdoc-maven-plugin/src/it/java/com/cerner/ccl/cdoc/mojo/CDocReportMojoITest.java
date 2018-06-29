package com.cerner.ccl.cdoc.mojo;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Integration tests of the {@code cdoc-report} mojo.
 * 
 * @author Joshua Hyde
 * 
 */

public class CDocReportMojoITest {
    /**
     * A {@link Rule} used to obtain the current test name.
     */
    @Rule
    public TestName testName = new TestName();

    private static Properties originalSystemProperties = System.getProperties();
    private final List<String> siteGoal = Collections.singletonList("site");
    private final WebDriver driver = new HtmlUnitDriver();
    
    private Properties getProperties() {
        final Properties mavenProperties = new Properties();
        final Properties systemProperties = System.getProperties();
        for (final Entry<Object, Object> systemProperty : systemProperties.entrySet())
            if (systemProperty.getKey() instanceof String && ((String) systemProperty.getKey()).startsWith("ccl-"))
                mavenProperties.put(systemProperty.getKey(), systemProperty.getValue());

        return mavenProperties;
    }

    private InvocationRequest getInvocationRequest(String projectId, List<String> goals, File logFile)
            throws FileNotFoundException, URISyntaxException {
        return new DefaultInvocationRequest().setPomFile(getPom(projectId)).setProperties(getProperties())
                .setGoals(goals).setOutputHandler(new PrintStreamHandler(new PrintStream(logFile), true));
    }

    private InvocationResult executeMaven(String projectId, List<String> goals, File logFile)
            throws FileNotFoundException, URISyntaxException, MavenInvocationException {
        InvocationRequest request = getInvocationRequest(projectId, goals, logFile);
        return new DefaultInvoker().execute(request);
    }

    /**
     * If the project is configured to exclude certain resources, then they should not have reports generated for them.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExcludeFiles() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("excluded-script", siteGoal, logFile);
        assertThat(result.getExitCode()).isZero();

        final File cdocDirectory = new File(getProjectDirectory("excluded-script"), "target/site/cdoc-report");
        assertThat(cdocDirectory.list(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return StringUtils.endsWithIgnoreCase(name, ".html");
            }
        })).containsOnly("doc-prg.html", "doc-inc.html");
    }

    /**
     * Site generation should not fail if no output encoding is specified in the POM.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNoOutputEncoding() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("no-output-encoding", siteGoal, logFile);
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Test that a summary is not generated if the project only has one script.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSingleScript() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("single-script", siteGoal, logFile);
        assertThat(result.getExitCode()).isZero();

        final WebDriver driver = navigateToCDocReport("single-script");
        assertThat(driver.getTitle()).isEqualTo("CDoc - cdoc_single_script");
    }

    /**
     * Create a log file to which a Maven invocation can write its output.
     * 
     * @return A {@link File} representing a location to which the Maven invocation can write its output.
     */
    private File getLogFile() {
        final File logDirectory = new File("target/logs/" + getClass().getSimpleName());
        if (!logDirectory.exists())
            try {
                FileUtils.forceMkdir(logDirectory);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create log directory.", e);
            }

        return new File(logDirectory, testName.getMethodName() + ".log");
    }

    /**
     * Get a project's POM. It is assumed that the file is located beneath {@code /test-projects} on the classpath.
     * 
     * @param artifactId
     *            The artifact ID of the project for which the POM is to be retrieved.
     * @return A {@link File} reference representing the desired POM file.
     * @throws FileNotFoundException
     *             If the given file cannot be found on the classpath.
     * @throws URISyntaxException
     *             If converting the {@link URL} representing the file on the classpath cannot be converted into a {@link URI}.
     */
    private File getPom(final String artifactId) throws FileNotFoundException, URISyntaxException {
        final File pomFile = new File(getProjectDirectory(artifactId), "pom.xml");
        if (!pomFile.exists())
            throw new FileNotFoundException("No POM found for project: " + artifactId);

        return pomFile;
    }

    /**
     * Get a project's project directory.
     * 
     * @param artifactId
     *            The artifact ID of the project for which the project is to be retrieved.
     * @return A {@link File} representing the given project's project directory.
     * @throws FileNotFoundException
     *             If the given project's project directory cannot be found.
     */
    private File getProjectDirectory(final String artifactId) throws FileNotFoundException {
        final URL projectUrl = getClass().getResource("/it-projects/" + artifactId);
        if (projectUrl == null)
            throw new FileNotFoundException("Project not found: " + artifactId);
        return FileUtils.toFile(projectUrl);
    }

    /**
     * Navigate to the CDoc report in the Maven site.
     * 
     * @param artifactId
     *            The artifact ID of the project for which the CDoc report is to be navigated.
     * @return A {@link WebDriver} to be used to interact with the CDoc report.
     * @throws Exception
     */
    private WebDriver navigateToCDocReport(final String artifactId) throws Exception {
        final File indexFile = new File(getProjectDirectory(artifactId), "target/site/index.html");
        driver.get(indexFile.toURI().toURL().toExternalForm());

        driver.findElement(By.linkText("Project Reports")).click();
        driver.findElement(By.linkText("CDoc")).click();

        return driver;
    }
}
