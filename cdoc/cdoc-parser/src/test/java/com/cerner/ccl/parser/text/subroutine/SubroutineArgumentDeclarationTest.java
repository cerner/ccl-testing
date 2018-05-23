package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.DataType;

/**
 * Unit tests for {@link SubroutineArgumentDeclaration}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineArgumentDeclarationTest extends AbstractBeanUnitTest<SubroutineArgumentDeclaration> {
    /**
     * Constructing an explicitly by-ref argument with a {@code null} name should fail.
     */
    @Test
    public void testConstructByRefNullName() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Name cannot be null.");
        new SubroutineArgumentDeclaration(null, true, DataType.DQ8);
    }

    /**
     * Construction of an implicitly by-value argument with a {@code null} name should fail.
     */
    @Test
    public void testConstructByValNullName() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Name cannot be null.");
        new SubroutineArgumentDeclaration(null, DataType.F8);
    }

    /**
     * Construction of an untyped argument with a {@code null} name should fail.
     */
    @Test
    public void testConstructUntypedNullName() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Name cannot be null.");
        new SubroutineArgumentDeclaration(null);
    }

    /**
     * Two arguments of the same values should be equal.
     */
    @Test
    public void testEquals() {
        final SubroutineArgumentDeclaration first = new SubroutineArgumentDeclaration("an_arg", DataType.F8);
        final SubroutineArgumentDeclaration other = new SubroutineArgumentDeclaration(first.getName(),
                first.getDataType());
        assertThat(first).isEqualTo(other);
        assertThat(other).isEqualTo(first);

        assertThat(first.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Two arguments of different names should not be equal.
     */
    @Test
    public void testEqualsDifferentName() {
        final SubroutineArgumentDeclaration first = new SubroutineArgumentDeclaration("an_arg");
        final SubroutineArgumentDeclaration other = new SubroutineArgumentDeclaration(
                StringUtils.reverse(first.getName()));
        assertThat(first).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(first);
    }

    /**
     * Two arguments of different referenceability should not be equal.
     */
    @Test
    public void testEqualsDifferentReferencability() {
        final SubroutineArgumentDeclaration first = new SubroutineArgumentDeclaration("an_arg", true, DataType.DQ8);
        final SubroutineArgumentDeclaration other = new SubroutineArgumentDeclaration(first.getName(), !first.isByRef(),
                first.getDataType());
        assertThat(first).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(first);
    }

    /**
     * Two arguments of different data types should not be equal. This largely exists to test the condition of when the
     * superclass indicates an inequality.
     */
    @Test
    public void testEqualsDifferentDataType() {
        final SubroutineArgumentDeclaration first = new SubroutineArgumentDeclaration("test", DataType.DQ8);
        final SubroutineArgumentDeclaration other = new SubroutineArgumentDeclaration(first.getName(), DataType.F8);
        assertThat(first).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(new SubroutineArgumentDeclaration("an_arg").getName()).isEqualTo("an_arg");
    }

    /**
     * The by-referenceability of an argument should reflect its explicit setting.
     */
    @Test
    public void testIsByRef() {
        assertThat(new SubroutineArgumentDeclaration("by_ref", true, DataType.DQ8).isByRef()).isTrue();
    }

    /**
     * Test when by-referenceability is implicitly set.
     */
    @Test
    public void testIsByRefImplicit() {
        assertThat(new SubroutineArgumentDeclaration("by_val").isByRef()).isFalse();
        assertThat(new SubroutineArgumentDeclaration("by_val_typed", DataType.F8).isByRef()).isFalse();
    }

    @Override
    protected SubroutineArgumentDeclaration getBean() {
        return new SubroutineArgumentDeclaration("a name", true, DataType.F8);
    }

    @Override
    protected SubroutineArgumentDeclaration newBeanFrom(final SubroutineArgumentDeclaration otherBean) {
        return new SubroutineArgumentDeclaration(otherBean.getName(), otherBean.isByRef(), otherBean.getDataType());
    }
}
