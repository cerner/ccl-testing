package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link ScriptArgument}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class ScriptArgumentTest extends AbstractBeanUnitTest<ScriptArgument> {
    private final String description = "i am the description";
    private final ScriptArgument argument = new ScriptArgument(description);

    /**
     * Construction with a {@code null} description should fail.
     */
    @Test
    public void testConstructNullDescription() {
        expect(IllegalArgumentException.class);
        expect("Description cannot be null.");
        new ScriptArgument(null);
    }


    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(argument.getDescription()).isEqualTo(description);
    }

    @Override
    protected ScriptArgument getBean() {
        return argument;
    }

    @Override
    protected ScriptArgument newBeanFrom(final ScriptArgument otherBean) {
        return new ScriptArgument(otherBean.getDescription());
    }
}
