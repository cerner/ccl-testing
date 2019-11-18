package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Locale;

import org.junit.Test;

import com.cerner.ccl.parser.exception.InvalidDataTypeDeclarationException;

/**
 * Unit tests for {@link DataType}.
 *
 * @author Joshua Hyde
 *
 */

public class DataTypeTest {
    /**
     * Test the resolution of a declaration to a data type. Added two new test statements for the new data types added
     * (gvc and dq12).
     */
    @Test
    public void testForDeclaration() {
        assertThat(DataType.forDeclaration("i1")).isEqualTo(DataType.I1);
        assertThat(DataType.forDeclaration("i2")).isEqualTo(DataType.I2);
        assertThat(DataType.forDeclaration("i4")).isEqualTo(DataType.I4);
        assertThat(DataType.forDeclaration("w8")).isEqualTo(DataType.W8);
        assertThat(DataType.forDeclaration("ui1")).isEqualTo(DataType.UI1);
        assertThat(DataType.forDeclaration("ui2")).isEqualTo(DataType.UI2);
        assertThat(DataType.forDeclaration("ui4")).isEqualTo(DataType.UI4);
        assertThat(DataType.forDeclaration("uw8")).isEqualTo(DataType.UW8);
        assertThat(DataType.forDeclaration("h")).isEqualTo(DataType.H);
        assertThat(DataType.forDeclaration("f4")).isEqualTo(DataType.F4);
        assertThat(DataType.forDeclaration("f8")).isEqualTo(DataType.F8);
        assertThat(DataType.forDeclaration("c9")).isEqualTo(DataType.CHAR);
        assertThat(DataType.forDeclaration("vc")).isEqualTo(DataType.VC);
        assertThat(DataType.forDeclaration("gvc")).isEqualTo(DataType.GVC);
        assertThat(DataType.forDeclaration("zvc")).isEqualTo(DataType.ZVC);
        assertThat(DataType.forDeclaration("zgvc")).isEqualTo(DataType.ZGVC);
        assertThat(DataType.forDeclaration("dq8")).isEqualTo(DataType.DQ8);
        assertThat(DataType.forDeclaration("dm12")).isEqualTo(DataType.DM12);
        assertThat(DataType.forDeclaration("dm14")).isEqualTo(DataType.DM14);
    }

    /**
     * Test the resolution of a fixed-length character declaration.
     */
    @Test
    public void testForDeclarationChar() {
        assertThat(DataType.forDeclaration("c32")).isEqualTo(DataType.CHAR);
    }

    /**
     * Resolving a {@code null} declaration should fail.
     */
    @Test
    public void testForDeclarationNullDeclaration() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            DataType.forDeclaration(null);
        });
        assertThat(e.getMessage()).isEqualTo("Declaration cannot be null.");
    }

    /**
     * If the declaration cannot be resolved, the lookup should fail.
     */
    @Test
    public void testForDeclarationUnrecognized() {
        final String declaration = "this is an invalid declaration  ";
        final String normalized = declaration.trim().toUpperCase(Locale.US);
        InvalidDataTypeDeclarationException e = assertThrows(InvalidDataTypeDeclarationException.class, () -> {
            DataType.forDeclaration(declaration);
        });
        assertThat(e.getMessage())
                .startsWith("Unrecognized declaration: " + declaration + "; normalized to " + normalized);
    }
}
