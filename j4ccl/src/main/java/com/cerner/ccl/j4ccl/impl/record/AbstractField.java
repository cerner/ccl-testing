package com.cerner.ccl.j4ccl.impl.record;

import com.cerner.ccl.j4ccl.record.Field;

/**
 * A skeleton implementation that provides basic reusable implementations of field-related methods.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractField implements Field {

    @Override
    public String toString() {
        return getName() + " [" + getType().toString() + "]";
    }

}
