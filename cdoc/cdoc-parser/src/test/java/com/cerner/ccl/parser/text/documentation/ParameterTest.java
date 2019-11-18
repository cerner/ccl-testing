package com.cerner.ccl.parser.text.documentation;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link Parameter}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class ParameterTest extends AbstractBeanUnitTest<Parameter> {
    private final String name = "parameter_name";
    private final String description = "i am the description";
    private final Parameter param = new Parameter(name, description);

    /**
     * A parameter constructed with only a name should simply have a blank string for a description.
     */
    @Test
    public void testConstructNoDocumentation() {
        assertThat(new Parameter(name).getDescription()).isEmpty();
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new Parameter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * The description should have no bearing on the equality of two parameters.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final Parameter other = new Parameter(name, StringUtils.reverse(description));
        assertThat(param).isEqualTo(other);
        assertThat(other).isEqualTo(param);
        assertThat(param.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Verify that names are compared case-insensitively for purposes of determining equality.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final Parameter other = new Parameter(StringUtils.swapCase(name));
        assertThat(param).isEqualTo(other);
        assertThat(other).isEqualTo(param);
        assertThat(param.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(param.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(param.getName()).isEqualTo(name);
    }

    @Override
    protected Parameter getBean() {
        return param;
    }

    @Override
    protected Parameter newBeanFrom(final Parameter otherBean) {
        return new Parameter(otherBean.getName(), otherBean.getDescription());
    }

}
