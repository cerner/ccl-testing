package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetailsParser;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;

/**
 * Integration tests for {@link IncludeDocGenerator}.
 * 
 * @author Joshua Hyde
 * 
 */

public class IncludeDocGeneratorITest extends AbstractVelocityITest {
    private final ScriptExecutionDetailsParser detailsParser = new ScriptExecutionDetailsParser();
    private final Navigation backNavigation = new Navigation("Go Back", "../");

    /**
     * Test the generation of a report for an include file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testIncludeFile() throws Exception {
        final File file = getDestinationFile();
        final Writer writer = new FileWriter(file);
        try {
            final IncludeDocGenerator generator = new IncludeDocGenerator(getIncludeFile("cdoc_include.inc"), new File(getOutputDirectory(), "css"),
                    detailsParser.getDetails(readIncludeFile("cdoc_include.inc")), writer, backNavigation);
            generator.generate();
        } finally {
            writer.close();
        }

        final WebDriver driver = getDriver();
        driver.get(file.toURI().toURL().toExternalForm());

        assertThat(driver.getTitle()).isEqualTo("CDoc - cdoc_include.inc");
        assertThat(driver.findElement(By.id("includeDescription")).getText()).isEqualTo("This is a description of the include file.");
        assertThat(driver.findElement(By.id("objectFilename")).getText()).isEqualTo("cdoc_include.inc");

        final WebElement backNav = driver.findElement(By.id("backNavigationAnchor"));
        assertThat(backNav.getText()).isEqualTo(backNavigation.getAnchorText());
        assertThat(backNav.getAttribute("href")).isEqualTo(backNavigation.getDestination());

        final List<WebElement> recordStructureElements = driver.findElements(By.className("recordStructureDefinition"));
        assertThat(recordStructureElements).hasSize(2);

        /*
         * Verify request record structure
         */
        {
            final List<WebElement> rootMemberElements = recordStructureElements.get(0).findElements(By.className("recordStructureMember"));
            assertThat(rootMemberElements).hasSize(2);

            // person_id
            {
                final WebElement rootChar = rootMemberElements.get(0);
                assertThat(rootChar.getText()).isEqualTo("1 person_id = f8");
                rootChar.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = rootChar.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("The ID of the person.");
            }

            // cclsource:test_include.inc
            {
                final WebElement testInclude = rootMemberElements.get(1);
                assertThat(testInclude.getText()).isEqualTo("%i cclsource:test_include.inc");
            }
        }

        /*
         * Verify the reply record structure
         */
        {
            final List<WebElement> rootMemberElements = recordStructureElements.get(1).findElements(By.className("recordStructureMember"));
            assertThat(rootMemberElements).hasSize(1);

            // status
            {
                final WebElement status = rootMemberElements.get(0);
                assertThat(status.getText()).isEqualTo("1 status = c1");
                status.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = status.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("The status of the include file.");
            }
        }

        driver.findElement(By.id("subroutineVisibilityToggle")).click();

        /*
         * Verify the "some_sub" subroutine
         */
        {
            assertThat(driver.findElement(By.id("declaration-some_sub")).getText()).isEqualTo("some_sub ( arg1 = f8 , arg2 = vc ) = c32");
            assertThat(driver.findElement(By.id("description-some_sub")).getText()).isEqualTo("A subroutine.");
            assertThat(driver.findElement(By.id("some_sub&arg1")).getText()).isEqualTo("arg1 - The first argument.");
            assertThat(driver.findElement(By.id("some_sub&arg2")).getText()).isEqualTo("arg2 - The second argument.");
            assertThat(driver.findElement(By.id("returns-some_sub")).getText()).isEqualTo("\"filler\"");
        }
    }

}
