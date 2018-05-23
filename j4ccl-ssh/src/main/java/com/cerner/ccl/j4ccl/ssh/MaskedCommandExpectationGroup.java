package com.cerner.ccl.j4ccl.ssh;

/**
 *
 * An extension of the CommandExpectationGroup class which only has masked commands
 *
 * @author Fred Eckertson
 *
 */
public class MaskedCommandExpectationGroup extends CommandExpectationGroup {
    /**
     * Constructs a new MaskedCommandExpectationGroup instance.
     */
    public MaskedCommandExpectationGroup() {
        super(true);
    }
}
