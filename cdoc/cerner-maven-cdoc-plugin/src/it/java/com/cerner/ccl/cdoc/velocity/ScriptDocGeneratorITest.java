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
 * Integration tests for {@link ScriptDocGenerator}.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptDocGeneratorITest extends AbstractVelocityITest {
    private final ScriptExecutionDetailsParser detailsParser = new ScriptExecutionDetailsParser();
    private final Navigation backNavigation = new Navigation("Go Back From Whence Ye Came", "../back.html");

    /**
     * Test the documentation of a bound script.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBoundScript() throws Exception {
        final File file = getDestinationFile();
        final Writer writer = new FileWriter(file);
        try {
            final ScriptDocGenerator generator = new ScriptDocGenerator(getScript("cdoc_bound_script"), new File(getOutputDirectory(), "css"),
                    detailsParser.getDetails(readCclScript("cdoc_bound_script")), writer, backNavigation);
            generator.generate();
        } finally {
            writer.close();
        }

        final WebDriver driver = getDriver();
        driver.get(file.toURI().toURL().toExternalForm());

        assertThat(driver.getTitle()).isEqualTo("CDoc - cdoc_bound_script");
        assertThat(driver.findElement(By.id("scriptDescription")).getText()).isEqualTo("This is a script that is bound to a transaction.");
        assertThat(driver.findElement(By.id("objectFilename")).getText()).isEqualTo("cdoc_bound_script.prg");
        assertThat(driver.findElement(By.id("boundTransaction")).getText()).isEqualTo("Transaction: 12349876");

        final WebElement backNav = driver.findElement(By.id("backNavigationAnchor"));
        assertThat(backNav.getText()).isEqualTo(backNavigation.getAnchorText());
        assertThat(backNav.getAttribute("href")).isEqualTo(backNavigation.getDestination());

        final List<WebElement> recordStructureElements = driver.findElements(By.className("recordStructureDefinition"));
        assertThat(recordStructureElements).hasSize(2);

        /*
         * Verify request record structure
         */
        {
            assertThat(driver.findElement(By.id("requestRecordDescription")).getText()).isEqualTo("This is the request record structure");
            final List<WebElement> rootMemberElements = recordStructureElements.get(0).findElements(By.className("recordStructureMember"));
            assertThat(rootMemberElements).hasSize(9);

            // rootChar
            {
                final WebElement rootChar = rootMemberElements.get(0);
                assertThat(rootChar.getText()).isEqualTo("1 rootChar = c18");
                rootChar.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = rootChar.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("This is a character field at the root of the record structure.");
            }
            // rootList
            {
                final WebElement rootList = rootMemberElements.get(1);
                assertThat(rootList.getText()).isEqualTo("1 rootList [*]");
                rootList.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = rootList.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("This is a list at the root of the record structure");
            }
            // nested_field
            {
                final WebElement nestedField = rootMemberElements.get(2);
                assertThat(nestedField.getText()).isEqualTo("2 nested_field = i2");
                nestedField.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = nestedField.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).contains("This is an I2 in the nested list");
                assertThat(detailsDiv.getText()).contains("The population of this field is optional.");

                final List<WebElement> valueElements = detailsDiv.findElement(By.tagName("ul")).findElements(By.tagName("li"));
                assertThat(valueElements).hasSize(2);

                assertThat(valueElements.get(0).getText()).isEqualTo("\"0\" - False");
                assertThat(valueElements.get(1).getText()).isEqualTo("\"1\" - True");
            }
            // nested_list
            {
                final WebElement nestedList = rootMemberElements.get(3);
                assertThat(nestedList.getText()).isEqualTo("2 nested_list [1]");
                nestedList.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = nestedList.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("This is a list nested within a list - it's almost I N C E P T I O N");
            }
            // nested_nested_field
            {
                final WebElement nestedNestedField = rootMemberElements.get(4);
                assertThat(nestedNestedField.getText()).isEqualTo("3 nested_nested_field = dq8");
                nestedNestedField.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = nestedNestedField.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("A date field within the nested list");
            }
            assertThat(rootMemberElements.get(5).getText()).isEqualTo("3 inception_list [*]");
            assertThat(rootMemberElements.get(6).getText()).isEqualTo("4 inception_ind = i2");
            assertThat(rootMemberElements.get(7).getText()).isEqualTo("2 nested_dq8 = dq8");
            // rootF8
            {
                final WebElement rootF8 = rootMemberElements.get(8);
                assertThat(rootF8.getText()).isEqualTo("1 rootF8 = f8");
            }
        }

        /*
         * Verify the reply record structure
         */
        {
            assertThat(driver.findElement(By.id("replyRecordDescription")).getText()).isEqualTo("The reply record structure");
            final List<WebElement> rootMemberElements = recordStructureElements.get(1).findElements(By.className("recordStructureMember"));
            assertThat(rootMemberElements).hasSize(8);

            // status
            {
                final WebElement status = rootMemberElements.get(0);
                assertThat(status.getText()).isEqualTo("1 status = c1");
                status.findElement(By.tagName("a")).click();
                final WebElement detailsDiv = status.findElement(By.tagName("div")).findElement(By.tagName("div"));
                assertThat(detailsDiv.getText()).isEqualTo("A single-character status indicator of the result of this script.");
            }

            // status_data
            {
                assertThat(rootMemberElements.get(1).getText()).isEqualTo("1 status_data");
                assertThat(rootMemberElements.get(2).getText()).isEqualTo("2 status = c1");
                assertThat(rootMemberElements.get(3).getText()).isEqualTo("2 subeventstatus [1]");
                assertThat(rootMemberElements.get(4).getText()).isEqualTo("3 OperationName = c25");
                assertThat(rootMemberElements.get(5).getText()).isEqualTo("3 OperationStatus = c1");
                assertThat(rootMemberElements.get(6).getText()).isEqualTo("3 TargetObjectName = c25");
                assertThat(rootMemberElements.get(7).getText()).isEqualTo("3 TargetObjectValue = vc");
            }
        }

        driver.findElement(By.id("subroutineVisibilityToggle")).click();

        /*
         * Verify the "some_subroutine" subroutine
         */
        {
            assertThat(driver.findElement(By.id("declaration-some_subroutine")).getText()).isEqualTo("some_subroutine ( arg1 = f8 , arg2 = c234 ) = c74");
            assertThat(driver.findElement(By.id("description-some_subroutine")).getText()).isEqualTo("A subroutine.");
            assertThat(driver.findElement(By.id("some_subroutine&arg1")).getText()).isEqualTo("arg1 - Argument 1");
            assertThat(driver.findElement(By.id("some_subroutine&arg2")).getText()).isEqualTo("arg2 - Argument 2");
            assertThat(driver.findElement(By.id("returns-some_subroutine")).getText()).isEqualTo("74 characters");
        }

        /*
         * Verify script execution warnings
         */
        {
            final List<WebElement> executionWarnings = driver.findElement(By.id("scriptExecutionWarnings")).findElements(By.tagName("li"));
            assertThat(executionWarnings).hasSize(1);
            assertThat(executionWarnings.get(0).getText()).isEqualTo("{52} set stat = callprg(\"test_script\")");
        }
    }

    /**
     * Test the generation of a report for a command-line script.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCliScript() throws Exception {
        final File file = getDestinationFile();
        final Writer writer = new FileWriter(file);
        try {
            final ScriptDocGenerator generator = new ScriptDocGenerator(getScript("cdoc_cli_script"), new File(getOutputDirectory(), "css"),
                    detailsParser.getDetails(readCclScript("cdoc_cli_script")), writer, backNavigation);
            generator.generate();
        } finally {
            writer.close();
        }

        final WebDriver driver = getDriver();
        driver.get(file.toURI().toURL().toExternalForm());

        assertThat(driver.getTitle()).isEqualTo("CDoc - cdoc_cli_script");
        assertThat(driver.findElement(By.id("scriptDescription")).getText()).isEqualTo("This is the definition of a script that takes command-line arguments.");
        assertThat(driver.findElement(By.id("objectFilename")).getText()).isEqualTo("cdoc_cli_script.prg");

        final WebElement backNav = driver.findElement(By.id("backNavigationAnchor"));
        assertThat(backNav.getText()).isEqualTo(backNavigation.getAnchorText());
        assertThat(backNav.getAttribute("href")).isEqualTo(backNavigation.getDestination());

        /*
         * Verify script arguments
         */
        assertThat(driver.findElement(By.id("scriptArgument-1")).getText()).isEqualTo("This should be the first argument.");
        assertThat(driver.findElement(By.id("scriptArgument-2")).getText()).isEqualTo("This should be the second argument.");

        driver.findElement(By.id("subroutineVisibilityToggle")).click();

        /*
         * Verify the "hasDeclaration" subroutine
         */
        {
            assertThat(driver.findElement(By.id("declaration-hasDeclaration")).getText()).isEqualTo("hasDeclaration ( arg1 = c23 , arg2 = i4 (REF) ) = c32");
            assertThat(driver.findElement(By.id("description-hasDeclaration")).getText()).isEqualTo("This is a subroutine with a declaration.");

            /*
             * Arguments
             */
            assertThat(driver.findElement(By.id("hasDeclaration&arg1")).getText()).isEqualTo("arg1 - This is the first argument of the subroutine.");
            assertThat(driver.findElement(By.id("hasDeclaration&arg2")).getText()).isEqualTo("arg2 - This is the second argument of the subroutine.");

            assertThat(driver.findElement(By.id("returns-hasDeclaration")).getText()).isEqualTo("The word \"useless!\"");
        }

        /*
         * Verify the "hasNoDeclaration" subroutine
         */
        {
            assertThat(driver.findElement(By.id("declaration-hasNoDeclaration")).getText()).isEqualTo("hasNoDeclaration ( noDeclarationArg )");
            assertThat(driver.findElement(By.id("description-hasNoDeclaration")).getText()).isEqualTo("This subroutine has no declaration.");

            /*
             * Arguments
             */
            assertThat(driver.findElement(By.id("hasNoDeclaration&noDeclarationArg")).getText()).isEqualTo("noDeclarationArg - This is the only argument in this subroutine.");

            assertThat(driver.findElement(By.id("returns-hasNoDeclaration")).getText()).isEqualTo("0");
        }

        /*
         * Verify the "hasNoArgs" subroutine
         */
        {
            assertThat(driver.findElement(By.id("declaration-hasNoArgs")).getText()).isEqualTo("hasNoArgs ( VOID ) = i2");
            assertThat(driver.findElement(By.id("description-hasNoArgs")).getText()).isEqualTo("This subroutine takes no arguments.");
            assertThat(driver.findElement(By.id("returns-hasNoArgs")).getText()).isEqualTo("The value of -1");
        }

        /*
         * Verify script execution details
         */
        {
            final List<WebElement> executedScripts = driver.findElement(By.id("executedScripts")).findElements(By.tagName("span"));
            assertThat(executedScripts).hasSize(1);
            assertThat(executedScripts.get(0).getText()).isEqualTo("example_test_script");
        }

        /*
         * Verify script execution warnings
         */
        {
            final List<WebElement> executionWarnings = driver.findElement(By.id("scriptExecutionWarnings")).findElements(By.tagName("li"));
            assertThat(executionWarnings).hasSize(1);
            assertThat(executionWarnings.get(0).getText()).isEqualTo("{15} execute value(valueName)");
        }
    }
}
