package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.exception.AnalysisRuleProvider;

/**
 * These are simple tests aimed at ensuring that the rules can handle costly operations, such as analysis of large
 * programs.
 *
 * @author Joshua Hyde
 *
 */

public class StressTest extends AbstractJDomTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StressTest.class);
    private static boolean doStressTest;

    /**
     * Read the indicator of whether or not to run tests from a properties file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @BeforeClass
    public static void readStressTestIndicator() throws Exception {
        try (final InputStream propertiesStream = StressTest.class.getResourceAsStream("/tests.properties")) {
            final PropertyResourceBundle bundle = new PropertyResourceBundle(propertiesStream);
            doStressTest = Boolean.valueOf(bundle.getString("doStressTest"));
        }
    }

    /**
     * Test the analysis of a large program to ensure that the rules can tolerate them.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testLargeProgram() throws Exception {
        if (!doStressTest) {
            LOGGER.trace("Stress testing has been disabled and will not run for test {}", getTestName());
            return;
        }

        StringBuilder source = new StringBuilder();
        try (InputStream is = StressTest.class.getResourceAsStream("/ccl/xml/StressTest/large-ccl-program.xml");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            assertThat(is).isNotNull();
            String line = "";
            while ((line = br.readLine()) != null) {
                source.append(line);
            }
        }

        for (AnalysisRule rule : new AnalysisRuleProvider().getRules()) {
            rule.analyze(source.toString());
        }
    }

    /**
     * Test the analysis of a large program to ensure that the rules can tolerate them.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testRegression() throws Exception {
        if (!doStressTest) {
            LOGGER.trace("Stress testing has been disabled and will not run for test {}", getTestName());
            return;
        }

        StringBuilder source = new StringBuilder();
        try (InputStream is = StressTest.class.getResourceAsStream("/ccl/xml/StressTest/regression.xml");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            assertThat(is).isNotNull();
            String line = "";
            while ((line = br.readLine()) != null) {
                source.append(line);
            }
        }

        for (AnalysisRule rule : new AnalysisRuleProvider().getRules()) {
            rule.analyze(source.toString());
        }
    }
}
