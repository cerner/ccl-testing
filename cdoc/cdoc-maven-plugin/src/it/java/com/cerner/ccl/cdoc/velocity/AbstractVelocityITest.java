package com.cerner.ccl.cdoc.velocity;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.cerner.ccl.cdoc.AbstractSourceReadingITest;

/**
 * Skeleton definition of an integration test for Apache Velocity-related code.
 * 
 * @author Joshua Hyde
 * 
 */

public abstract class AbstractVelocityITest extends AbstractSourceReadingITest {
    /**
     * A {@link Rule} used to obtain the current test name.
     */
    @Rule
    public TestName testName = new TestName();

    private final File outputDirectory = new File("target/" + getClass().getSimpleName());
    private final WebDriver driver = new HtmlUnitDriver(true);

    /**
     * Start from a clean slate for each test.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        if (!getOutputDirectory().exists()) {
            FileUtils.forceMkdir(getOutputDirectory());
        }
    }

    /**
     * Shut down the driver after each test.
     */
    @After
    public void tearDown() {
        if (driver != null) {
            driver.close();
        }
    }

    /**
     * Get the file to which the webpage is to be written.
     * 
     * @return A {@link File} to which the webpage is to be written.
     */
    protected File getDestinationFile() {
        return new File(getOutputDirectory(), testName.getMethodName() + ".html");
    }

    /**
     * Get the web driver that can be used to interact with the generated pages.
     * 
     * @return A {@link WebDriver}.
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Get the directory to which generated HTML files should be output.
     * 
     * @return A {@link File} representing the output directory.
     */
    protected File getOutputDirectory() {
        return outputDirectory;
    }
}
