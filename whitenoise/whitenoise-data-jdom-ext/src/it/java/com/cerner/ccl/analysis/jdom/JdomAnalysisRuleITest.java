package com.cerner.ccl.analysis.jdom;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.junit.Test;

import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;
import com.cerner.ccl.analysis.exception.AnalysisRuleProvider;

/**
 * Integration tests for {@link JdomAnalysisRule}.
 *
 * @author Joshua Hyde
 *
 */
public class JdomAnalysisRuleITest {
    /**
     * Test the execution of an analysis.
     */
    @Test
    public void testAnalyze() {
        final String xml = "<TEST><A>TEST_A</A><B>TEST_B</B></TEST>";
        final Set<AnalysisRule> rules = new AnalysisRuleProvider().getRules();
        assertThat(rules).hasSize(1);

        final Set<Violation> violations = rules.iterator().next().analyze(xml);
        assertThat(violations).hasSize(1);

        final Violation violation = violations.iterator().next();
//        assertThat(violation.getViolationDescription()).isEqualTo("TEST_B");
    }

    /**
     * Test the execution of an analysis.
     */
    @Test
    public void testGetCheckedViolations() {
        final Set<AnalysisRule> rules = new AnalysisRuleProvider().getRules();
        assertThat(rules).hasSize(1);

        final Set<Violation> violations = rules.iterator().next().getCheckedViolations();
        assertThat(violations).hasSize(1);

        final Violation v = violations.iterator().next();

        assertThat(v.getViolationDescription()).isSameAs("Description");
    }

    /**
     * A simple delegate to facilitate testing.
     *
     * @author Joshua Hyde
     *
     */
    public static class ExampleDelegate extends JdomAnalysisRule.Delegate {
        @SuppressWarnings("javadoc")
        public ExampleDelegate(Document document) {
            super(document);
        }

        @Override
        protected Set<Violation> analyze() {
            final String description = "Description"; // selectNodes(document, "/TEST/B").get(0).getText();
            return Collections.<Violation> singleton(new Violation() {

                public String getViolationDescription() {
                    return description;
                }

                public ViolationId getViolationId() {
                    return new ViolationId("TEST", "VIOLATION");
                }

                public Integer getLineNumber() {
                    return null;
                }

                public String getViolationExplanation() {
                    return "bloop";
                }

            });
        }

        @Override
        public Set<Violation> getCheckedViolations() {
            final Set<Violation> violations = new HashSet<Violation>();

            violations.add(new Violation() {

                public ViolationId getViolationId() {
                    return new ViolationId("CORE", "ABC");
                }

                public String getViolationExplanation() {
                    return "Explanation";
                }

                public String getViolationDescription() {
                    return "Description";
                }

                public Integer getLineNumber() {
                    return 22;
                }
            });

            return violations;
        }

    }
}
