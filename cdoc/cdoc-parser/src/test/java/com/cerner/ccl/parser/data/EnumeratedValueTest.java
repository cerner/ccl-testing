package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link EnumeratedValue}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class EnumeratedValueTest extends AbstractBeanUnitTest<EnumeratedValue> {
    private final String value = "i am the value";
    private final String description = "i am the description";
    private final EnumeratedValue enumerated = new EnumeratedValue(value, description);

    /**
     * Construction with a {@code null} description should produce an empty description.
     */
    @Test
    public void testConstructNullDescription() {
        assertThat(new EnumeratedValue(value, null).getDescription()).isNotNull().isEmpty();
    }

    /**
     * Construction with a {@code null} value should fail.
     */
    @Test
    public void testConstructNullValue() {
        expect(IllegalArgumentException.class);
        expect("Value cannot be null.");
        new EnumeratedValue(null);
    }

    /**
     * The description should not be considered when determining equality of two values.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final EnumeratedValue other = new EnumeratedValue(value, StringUtils.reverse(description));
        assertThat(enumerated).isEqualTo(other);
        assertThat(other).isEqualTo(enumerated);
        assertThat(other.hashCode()).isEqualTo(enumerated.hashCode());
    }

    /**
     * Two enumerated values with different values should be inequal.
     */
    @Test
    public void testEqualsDifferentValue() {
        final EnumeratedValue other = new EnumeratedValue(StringUtils.reverse(value), description);
        assertThat(enumerated).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(enumerated);
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(enumerated.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of a value.
     */
    @Test
    public void testGetValue() {
        assertThat(enumerated.getValue()).isEqualTo(value);
    }

    @Override
    protected EnumeratedValue getBean() {
        return enumerated;
    }

    @Override
    protected EnumeratedValue newBeanFrom(final EnumeratedValue otherBean) {
        return new EnumeratedValue(otherBean.getValue(), otherBean.getDescription());
    }
}
