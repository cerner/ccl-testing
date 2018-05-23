package com.cerner.ccl.j4ccl.adders;

import com.cerner.ccl.j4ccl.CclExecutor;

/**
 * Defines an object that adds a CCL action to an action queue.
 *
 * @author Joshua Hyde
 * @see CclExecutor
 */

public interface Adder {
    /**
     * Commit a command to a command queue.
     *
     * @throws IllegalStateException
     *             If the adder is not yet in a state in which it can be added to the queue.
     */
    void commit();
}
