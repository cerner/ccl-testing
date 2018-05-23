package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link IncludeDocumentation}.
 * 
 * @author Joshua Hyde
 * 
 */

public class IncludeDocumentationTest extends AbstractBeanUnitTest<IncludeDocumentation> {
    private final String description = "i am the description";
    private final IncludeDocumentation doc = new IncludeDocumentation(description);

    /**
     * Test the construction of an empty documentation.
     */
    @Test
    public void testConstructEmptyDoc() {
        final IncludeDocumentation empty = new IncludeDocumentation();
        assertThat(empty.getDescription()).isEmpty();
    }

    /**
     * Two objects with different descriptions should be inequal.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final IncludeDocumentation other = new IncludeDocumentation(StringUtils.reverse(description));
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(doc.getDescription()).isEqualTo(description);
    }

    @Override
    protected IncludeDocumentation getBean() {
        return doc;
    }

    @Override
    protected IncludeDocumentation newBeanFrom(final IncludeDocumentation otherBean) {
        return new IncludeDocumentation(otherBean.getDescription());
    }

}
