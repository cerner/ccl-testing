package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * Unit tests for {@link AbstractPropertyReflectingFilter}.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractPropertyReflectingFilterTest {
    private final ConcreteFilter filter = new ConcreteFilter();

    /**
     * Test the retrieval of a value for a property from a field within the class.
     */
    @Test
    public void testGetInternalValueField() {
        final String expectedValue = "an.expected.value";
        final Violation toFilter = new ViolationStub() {
            @SuppressWarnings("unused")
            private final String internalProperty = expectedValue;
        };
        assertThat(filter.getInternalValue(toFilter, "internalProperty")).isEqualTo(expectedValue);
    }

    /**
     * Test the retrieval of a value by a getter.
     */
    @Test
    public void testGetInternalValueGetter() {
        final String expectedValue = "the.expected.value";
        final Violation toFilter = new ViolationStub() {
            @SuppressWarnings("unused")
            private String getInternalProperty() {
                return expectedValue;
            }
        };
        assertThat(filter.getInternalValue(toFilter, "internalProperty")).isEqualTo(expectedValue);
    }

    /**
     * If the requested property cannot be found, then the retrieval of the internal value should simply be {@code null}.
     */
    @Test
    public void testGetInternalValueNotFound() {
        assertThat(filter.getInternalValue(new ViolationStub(), "thisWillNeverEverBeAProperty")).isNull();
    }

    /**
     * A concrete implementation of {@link AbstractPropertyReflectingFilter} for testing purposes.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteFilter extends AbstractPropertyReflectingFilter {
        public ConcreteFilter() {
        }

        /**
         * {@inheritDoc}
         */
        public boolean exclude(final String scriptName, final Violation violation) {
            return false;
        }
    }

    /**
     * A stub of a {@link Violation} to assist with testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ViolationStub implements Violation {
        public ViolationStub() {
        }

        /**
         * {@inheritDoc}
         */
        public Integer getLineNumber() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getViolationDescription() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getViolationExplanation() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public ViolationId getViolationId() {
            return null;
        }
    }
}
