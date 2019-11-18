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
 * Unit tests for {@link SubroutineDeclarationSmoosher}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDeclarationSmoosherTest extends AbstractIndexedSmoosherUnitTest<SubroutineDeclarationSmoosher> {
    private final SubroutineDeclarationSmoosher smoosher = new SubroutineDeclarationSmoosher();

    /**
     * Test the determination of the smooshability of a line.
     */
    @Test
    public void testCanSmoosh() {
        assertThat(smoosher.canSmoosh("declare some_subroutine(arg1 = f8) = null")).isTrue();
    }

    /**
     * Non-declarations should not be considered smooshable.
     */
    @Test
    public void testCanSmooshNotDeclaration() {
        assertThat(smoosher.canSmoosh("subroutine some_sub(arg)")).isFalse();
    }

    /**
     * Variable declarations should not be considered smooshable.
     */
    @Test
    public void testCanSmooshVariableDeclaration() {
        assertThat(smoosher.canSmoosh("declare some_var = vc")).isFalse();
        assertThat(smoosher.canSmoosh("declare some_other_var = i4 with protect, noconstant(0)")).isFalse();
    }

    /**
     * Test the smooshing of a multi-line subroutine declaration.
     */
    @Test
    public void testSmooshMultilineDeclaration() {
        final List<String> text = Arrays.asList("declare some_sub (", "arg1 = f8, arg2 = f8", ") = null",
                "garbage data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo("declare some_sub ( arg1 = f8, arg2 = f8 ) = null");
        assertThat(smoosher.getEndingIndex()).isEqualTo(2);
    }

    /**
     * If the declaration has no proper closing, then smooshing its definition should fail.
     */
    @Test
    public void testSmooshNoDeclarationClose() {
        final List<String> text = Collections.singletonList("declare incomplete_sub(arg1 = f8)");
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            smoosher.smoosh(0, text);
        });
        assertThat(e.getMessage()).isEqualTo("Unable to find close to subroutine declaration: " + text.get(0));
    }

    /**
     * Test the smooshing of a single-line declaration.
     */
    @Test
    public void testSmooshSingleLine() {
        final List<String> text = Arrays.asList("declare some_sub(void) = i2", "garbage data");
        assertThat(smoosher.smoosh(0, text)).isEqualTo(text.get(0));
        assertThat(smoosher.getEndingIndex()).isEqualTo(0);
    }

    @Override
    protected SubroutineDeclarationSmoosher getSmoosher() {
        return smoosher;
    }

}
