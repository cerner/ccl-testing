package com.cerner.ccl.j4ccl.impl.record.factory;

import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;

/**
 * Unit tests for {@link FieldImplFactory}.
 *
 * @author Joshua Hyde
 *
 */

public class FieldImplFactoryTest {
    /**
     * Verify that, if a primitive field for {@link DataType#CHARACTER} is requested, the build fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSimpleFieldCharacter() {
        new FieldImplFactory().createSimpleField("test", DataType.CHARACTER);
    }

}
