package com.cerner.ccl.parser.data;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link SimpleDataTyped}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SimpleDataTypedTest extends AbstractBeanUnitTest<SimpleDataTyped> {
    /**
     * Construction with a {@code null} data type should fail.
     */
    @Test
    public void testConstructNullDataType() {
        expect(IllegalArgumentException.class);
        expect("Data type cannot be null.");
        new SimpleDataTyped(null);
    }

    @Override
    protected SimpleDataTyped getBean() {
        return new SimpleDataTyped(DataType.VC);
    }

    @Override
    protected SimpleDataTyped newBeanFrom(final SimpleDataTyped otherBean) {
        return new SimpleDataTyped(otherBean.getDataType());
    }

}
