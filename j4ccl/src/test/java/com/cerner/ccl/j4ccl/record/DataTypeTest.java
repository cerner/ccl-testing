package com.cerner.ccl.j4ccl.record;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link DataType}
 *
 * @author Fred Eckertson
 *
 */
public class DataTypeTest {

    /**
     * Test the isComplexType method
     */
    @Test
    public void testIsComplexType() {
        assertThat(DataType.I2.isComplexType()).isFalse();
        assertThat(DataType.I4.isComplexType()).isFalse();
        assertThat(DataType.F8.isComplexType()).isFalse();
        assertThat(DataType.DQ8.isComplexType()).isFalse();
        assertThat(DataType.CHARACTER.isComplexType()).isFalse();
        assertThat(DataType.VC.isComplexType()).isFalse();

        assertThat(DataType.RECORD.isComplexType()).isTrue();

        assertThat(DataType.LIST.isComplexType()).isTrue();
        assertThat(DataType.DYNAMIC_LIST.isComplexType()).isTrue();
    }

    /**
     * Test the isList method
     */
    @Test
    public void testIsList() {
        assertThat(DataType.I2.isList()).isFalse();
        assertThat(DataType.I4.isList()).isFalse();
        assertThat(DataType.F8.isList()).isFalse();
        assertThat(DataType.DQ8.isList()).isFalse();
        assertThat(DataType.CHARACTER.isList()).isFalse();
        assertThat(DataType.VC.isList()).isFalse();

        assertThat(DataType.RECORD.isList()).isFalse();

        assertThat(DataType.LIST.isList()).isTrue();
        assertThat(DataType.DYNAMIC_LIST.isList()).isTrue();
    }

    /**
     * Test the isFixedLengthPrimitive method
     */
    @Test
    public void testIsFixedLengthPrimitive() {
        assertThat(DataType.I2.isFixedLengthPrimitive()).isTrue();
        assertThat(DataType.I4.isFixedLengthPrimitive()).isTrue();
        assertThat(DataType.F8.isFixedLengthPrimitive()).isTrue();
        assertThat(DataType.DQ8.isFixedLengthPrimitive()).isTrue();
        assertThat(DataType.CHARACTER.isFixedLengthPrimitive()).isTrue();
        assertThat(DataType.VC.isFixedLengthPrimitive()).isFalse();

        assertThat(DataType.RECORD.isFixedLengthPrimitive()).isFalse();

        assertThat(DataType.LIST.isFixedLengthPrimitive()).isFalse();
        assertThat(DataType.DYNAMIC_LIST.isFixedLengthPrimitive()).isFalse();
    }
}
