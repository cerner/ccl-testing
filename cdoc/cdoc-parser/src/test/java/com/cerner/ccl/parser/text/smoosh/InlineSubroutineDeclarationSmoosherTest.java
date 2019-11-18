package com.cerner.ccl.parser.text.smoosh;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;
import com.cerner.ccl.parser.text.smoosh.internal.AbstractIndexedSmoosherUnitTest;

/**
 * Unit tests for {@link InlineSubroutineDeclarationSmoosher}.
 *
 * @author Fred Eckertson
 *
 */

public class InlineSubroutineDeclarationSmoosherTest
        extends AbstractIndexedSmoosherUnitTest<InlineSubroutineDeclarationSmoosher> {
    private final InlineSubroutineDeclarationSmoosher smoosher = new InlineSubroutineDeclarationSmoosher();

    /**
     * Test the determination of a line's smooshability.
     */
    @Test
    public void testCanSmoosh() {
        assertThat(smoosher.canSmoosh("subroutine (some_sub(null) = null)")).isTrue();
        assertThat(smoosher.canSmoosh("subroutine some_sub(null) = null")).isFalse();
    }

    /**
     * Test the smooshing of a multi-line subroutine definition.
     */
    @Test
    public void testSmooshMultilineDefinition() {
        final List<String> text = Arrays.asList("subroutine(some_sub", "(arg1=i2, arg2=i4)", "=i4)", "garbage_data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo("subroutine(some_sub (arg1=i2, arg2=i4) =i4)");
        assertThat(smoosher.getEndingIndex()).isEqualTo(2);
    }

    /**
     * If there's no closure to the definition, then smooshing should fail
     */
    @Test
    public void testSmooshNoClose() {
        final List<String> text = Arrays.asList("subroutine (incomplete_sub (arg1=i4, arg2=i4)=", "null");
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            smoosher.smoosh(0, text);
        });
        assertThat(e.getMessage()).startsWith("Unable to find close to subroutine definition: " + text.get(0));
    }

    /**
     * Test the smooshing of a single-line declaration.
     */
    @Test
    public void testSmooshSingleLineDefinition() {
        final List<String> text = Arrays.asList("subroutine (some_sub (arg1=f8)=f8)", "garbage_data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo(text.get(0));
        assertThat(smoosher.getEndingIndex()).isEqualTo(0);
    }

    @Override
    protected InlineSubroutineDeclarationSmoosher getSmoosher() {
        return smoosher;
    }
}
