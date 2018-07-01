package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cerner.ccl.cdoc.mojo.data.Documentation;

/**
 * Integration tests for {@link SummaryGenerator}.
 * 
 * @author Joshua Hyde
 * 
 */

public class SummaryGeneratorITest extends AbstractVelocityITest {
    private final SummaryGenerator generator = new SummaryGenerator();

    /**
     * Test the generation of the summary.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerateSummary() throws Exception {
        final String projectName = "a-project-name";
        final String projectVersion = "1.1.1.1.1.1.1.1.1";
        final MavenProject project = mock(MavenProject.class);
        when(project.getName()).thenReturn(projectName);
        when(project.getVersion()).thenReturn(projectVersion);

        final String docADestinationFilename = "docA.html";
        final String docAObjectFilename = "docA.inc";
        final Documentation docA = mock(Documentation.class);
        when(docA.getDestinationFilename()).thenReturn(docADestinationFilename);
        when(docA.getObjectFilename()).thenReturn(docAObjectFilename);

        final String docBDestinationFilename = "docB.html";
        final String docBObjectFilename = "docB.prg";
        final Documentation docB = mock(Documentation.class);
        when(docB.getDestinationFilename()).thenReturn(docBDestinationFilename);
        when(docB.getObjectFilename()).thenReturn(docBObjectFilename);

        final List<Documentation> docs = Arrays.asList(docA, docB);
        final File destinationFile = getDestinationFile();
        final FileWriter writer = new FileWriter(destinationFile);
        try {
            generator.generate(project, docs, File.createTempFile("testGenerateSummary", null).getParentFile(), writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }

        final WebDriver driver = getDriver();
        driver.get(destinationFile.toURI().toURL().toExternalForm());
        assertThat(driver.findElement(By.id("projectTitle")).getText()).isEqualTo(projectName + " - " + projectVersion);

        final List<WebElement> tableRows = driver.findElement(By.id("docLinks")).findElements(By.tagName("tr"));
        assertThat(tableRows).hasSize(3);
        // Table header
        assertThat(tableRows.get(0).getText()).isEqualTo("Filename");

        final WebElement docACell = tableRows.get(1).findElement(By.tagName("td"));
        assertThat(docACell.getAttribute("class")).isEqualTo("highlighted");
        final WebElement docAAnchor = docACell.findElement(By.tagName("a"));
        assertThat(docAAnchor.getAttribute("href")).isEqualTo("./cdoc-report/" + docADestinationFilename);
        assertThat(docAAnchor.getText()).isEqualTo(docAObjectFilename);

        final WebElement docBCell = tableRows.get(2).findElement(By.tagName("td"));
        assertThat(docBCell.getAttribute("class")).isEqualTo("nonHighlighted");
        final WebElement docBAnchor = docBCell.findElement(By.tagName("a"));
        assertThat(docBAnchor.getAttribute("href")).isEqualTo("./cdoc-report/" + docBDestinationFilename);
        assertThat(docBAnchor.getText()).isEqualTo(docBObjectFilename);
    }
}
