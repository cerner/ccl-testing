package com.cerner.ccl.parser.text.smoosh;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.cerner.ccl.parser.text.smoosh.internal.AbstractIndexedSmoosherUnitTest;

/**
 * Unit tests for {@link SubroutineDefinitionHeaderSmoosher}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDefinitionHeaderSmoosherTest
        extends AbstractIndexedSmoosherUnitTest<SubroutineDefinitionHeaderSmoosher> {
    private final SubroutineDefinitionHeaderSmoosher smoosher = new SubroutineDefinitionHeaderSmoosher();

    /**
     * Test the determination of a line's smooshability.
     */
    @Test
    public void testCanSmoosh() {
        assertThat(smoosher.canSmoosh("subroutine some_sub(null)")).isTrue();
        assertThat(smoosher.canSmoosh("declare some_var = i2")).isFalse();
    }

    /**
     * Test the smooshing of a multi-line subroutine definition.
     */
    @Test
    public void testSmooshMultilineDefinition() {
        final List<String> text = Arrays.asList("subroutine some_sub ", "(arg1, arg2)", "garbage_data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo("subroutine some_sub (arg1, arg2)");
        assertThat(smoosher.getEndingIndex()).isEqualTo(1);
    }

    /**
     * If there's no closure to the definition, then smooshing should fail
     */
    @Test
    public void testSmooshNoClose() {
        final List<String> text = Collections.singletonList("subroutine incomplete_sub (");
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            smoosher.smoosh(0, text);
        });
        assertThat(e.getMessage()).isEqualTo("Unable to find close to subroutine definition: " + text.get(0));
    }

    /**
     * Test the smooshing of a single-line declaration.
     */
    @Test
    public void testSmooshSingleLineDefinition() {
        final List<String> text = Arrays.asList("subroutine some_sub (arg1)", "garbage_data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo(text.get(0));
        assertThat(smoosher.getEndingIndex()).isEqualTo(0);
    }

    @Override
    protected SubroutineDefinitionHeaderSmoosher getSmoosher() {
        return smoosher;
    }

}
