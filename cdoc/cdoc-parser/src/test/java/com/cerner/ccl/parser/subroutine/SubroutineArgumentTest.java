package com.cerner.ccl.parser.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;

/**
 * Unit tests for {@link SubroutineArgument}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineArgumentTest extends AbstractBeanUnitTest<SubroutineArgument> {
    private final String name = "argument_Name";
    private final DataType dataType = DataType.DQ8;
    private final boolean byRef = false;
    private final String description = "this is a description";
    private final SubroutineArgument argument = new SubroutineArgument(name, dataType, byRef, description);

    /**
     * Test the construction of a subroutine argument with absolutely no documentation.
     */
    @Test
    public void testConstructNoDocumentation() {
        final SubroutineArgument noDoc = new SubroutineArgument(name);
        assertThat(noDoc.getName()).isEqualTo(name);
        assertThat(noDoc.getDataType()).isNull();
        assertThat(noDoc.getDescription()).isEmpty();
        assertThat(noDoc.isByRef()).isFalse();
    }

    /**
     * Test the construction of a subroutine argument with no type information.
     */
    @Test
    public void testConstructNoTypeInformation() {
        final SubroutineArgument noType = new SubroutineArgument(name, description);
        assertThat(noType.getName()).isEqualTo(name);
        assertThat(noType.getDescription()).isEqualTo(description);
        assertThat(noType.getDataType()).isNull();
        assertThat(noType.isByRef()).isFalse();
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        expect(IllegalArgumentException.class);
        expect("Name cannot be null.");
        new SubroutineArgument(null);
    }

    /**
     * Data type should not be used in determining inequality.
     */
    @Test
    public void testEqualsDifferentDataType() {
        final SubroutineArgument other = new SubroutineArgument(name, DataType.F8, byRef, description);
        assertThat(argument).isEqualTo(other);
        assertThat(other).isEqualTo(argument);
        assertThat(argument.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * If two arguments have different descriptions, it should not affect determination of equality.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final SubroutineArgument other = new SubroutineArgument(name, dataType, byRef,
                StringUtils.reverse(description));
        assertThat(argument).isEqualTo(other);
        assertThat(other).isEqualTo(argument);
        assertThat(argument.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Two arguments by different names should be inequal.
     */
    @Test
    public void testEqualsDifferentName() {
        final SubroutineArgument other = new SubroutineArgument(StringUtils.reverse(name));
        assertThat(argument).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(argument);
    }

    /**
     * If two arguments have a different "referenceability" (i.e., by-value versus by-ref), it should not be used in the
     * determination of equality.
     */
    @Test
    public void testEqualsDifferentReferenceability() {
        final SubroutineArgument other = new SubroutineArgument(name, dataType, !byRef, description);
        assertThat(argument).isEqualTo(other);
        assertThat(other).isEqualTo(argument);
        assertThat(argument.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Comparison of arguments by name should be case-insensitive.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final SubroutineArgument other = new SubroutineArgument(StringUtils.swapCase(name));
        assertThat(argument).isEqualTo(other);
        assertThat(other).isEqualTo(argument);
        assertThat(other.hashCode()).isEqualTo(argument.hashCode());
    }

    /**
     * Test the retrieval of the data type.
     */
    @Test
    public void testGetDataType() {
        assertThat(argument.getDataType()).isEqualTo(dataType);
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(argument.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of the argument name.
     */
    @Test
    public void testGetName() {
        assertThat(argument.getName()).isEqualTo(name);
    }

    /**
     * Test the determination of referenceability.
     */
    @Test
    public void testIsByRef() {
        assertThat(argument.isByRef()).isEqualTo(byRef);
    }

    @Override
    protected SubroutineArgument getBean() {
        return argument;
    }

    @Override
    protected SubroutineArgument newBeanFrom(final SubroutineArgument otherBean) {
        return new SubroutineArgument(otherBean.getName(), otherBean.getDataType(), otherBean.isByRef(),
                otherBean.getDescription());
    }

}
