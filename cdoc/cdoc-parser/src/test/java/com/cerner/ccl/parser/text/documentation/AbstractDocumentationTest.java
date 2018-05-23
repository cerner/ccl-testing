package com.cerner.ccl.parser.text.documentation;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link AbstractDocumentation}.
 * 
 * @author Joshua Hyde
 * 
 */

public class AbstractDocumentationTest extends AbstractBeanUnitTest<AbstractDocumentation> {
    private final String description = "blech";
    private final ConcreteDocumentation doc = new ConcreteDocumentation(description);

    /**
     * A documentation object constructed with a {@code null} top-level description should merely return a blank
     * description.
     */
    @Test
    public void testConstructNullDescription() {
        assertThat(new ConcreteDocumentation(null).getDescription()).isEmpty();
    }

    /**
     * Two documentation objects with different descriptions should be inequal.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final ConcreteDocumentation other = new ConcreteDocumentation(StringUtils.reverse(description));
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * Test the retrieval of a description.
     */
    @Test
    public void testGetDescription() {
        assertThat(doc.getDescription()).isEqualTo(description);
    }

    @Override
    protected AbstractDocumentation getBean() {
        return doc;
    }

    @Override
    protected AbstractDocumentation newBeanFrom(final AbstractDocumentation otherBean) {
        return new ConcreteDocumentation(otherBean.getDescription());
    }

    /**
     * A concrete implementation of {@link AbstractDocumentation} for testing.
     * 
     * @author Joshua Hyde
     * 
     */
    private static class ConcreteDocumentation extends AbstractDocumentation {
        /**
         * Create documentation.
         * 
         * @param description
         *            The description of the object.
         */
        public ConcreteDocumentation(final String description) {
            super(description);
        }

    }

}
