package com.cerner.ccl.parser.text.documentation;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link SubroutineDocumentation}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDocumentationTest extends AbstractBeanUnitTest<SubroutineDocumentation> {
    private final String description = "i am the description";
    private final String returnDescription = "i am the return description";
    @Mock
    private Parameter parameter;
    private List<Parameter> parameters;
    private SubroutineDocumentation doc;

    /**
     * Set up the documentation for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        parameters = Collections.singletonList(parameter);
        doc = new SubroutineDocumentation(description, parameters, returnDescription);
    }

    /**
     * Test the construction of a documentation object with no documentation.
     */
    @Test
    public void testConstructNoDocumentation() {
        final SubroutineDocumentation undoc = new SubroutineDocumentation(null, null, null);
        assertThat(undoc.getParameters()).isEmpty();
        assertThat(undoc.getReturnDescription()).isEmpty();
    }

    /**
     * Two documentation objects with different parameters should be inequal.
     */
    @Test
    public void testEqualsDifferentParameters() {
        final SubroutineDocumentation other = new SubroutineDocumentation(description,
                Collections.<Parameter> emptyList(), returnDescription);
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * Two subroutine documentation with different return descriptions should be inequal.
     */
    @Test
    public void testEqualsDifferentReturnDescription() {
        final SubroutineDocumentation other = new SubroutineDocumentation(description, parameters,
                StringUtils.reverse(returnDescription));
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * Test the retrieval of the parameters.
     */
    @Test
    public void testGetParameters() {
        assertThat(doc.getParameters()).isEqualTo(parameters);
    }

    /**
     * Test the retrieval of the return description.
     */
    @Test
    public void testGetReturnDescription() {
        assertThat(doc.getReturnDescription()).isEqualTo(returnDescription);
    }

    @Override
    protected SubroutineDocumentation getBean() {
        return doc;
    }

    @Override
    protected SubroutineDocumentation newBeanFrom(final SubroutineDocumentation otherBean) {
        return new SubroutineDocumentation(otherBean.getDescription(), otherBean.getParameters(),
                otherBean.getReturnDescription());
    }

}
