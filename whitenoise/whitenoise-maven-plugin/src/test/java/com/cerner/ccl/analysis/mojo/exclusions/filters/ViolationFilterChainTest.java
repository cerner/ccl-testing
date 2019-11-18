package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.Builder;

/**
 * Unit tests for {@link ViolationFilterChain}.
 *
 * @author Joshua Hyde
 *
 */

public class ViolationFilterChainTest {
    private final Builder builder = ViolationFilterChain.build();

    /**
     * Verify that compound rules can be ANDed together properly.
     */
    @Test
    public void testExcludeCompoundExclusion() {
        final String scriptName = "a_script";
        final int lineNumber = 37;

        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(Integer.valueOf(lineNumber));

        final ViolationFilterChain chain = builder.withScriptName(scriptName).withLineNumber(lineNumber).build();
        assertThat(chain.exclude(scriptName, violation)).isTrue();
        assertThat(chain.exclude(StringUtils.reverse(scriptName), violation)).isFalse();
    }

    /**
     * Testing an exclusion with a {@code null} script name should fail.
     */
    @Test
    public void testExcludeNullScriptName() {
        final ViolationFilterChain chain = builder.build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            chain.exclude(null, mock(Violation.class));
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Testing for exclusion with a {@code null} {@link Violation} should fail.
     */
    @Test
    public void testExcludeNullViolation() {
        final ViolationFilterChain chain = builder.build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            chain.exclude("a_script", null);
        });
        assertThat(e.getMessage()).isEqualTo("Violation cannot be null.");
    }

    /**
     * Test exclusions with a line number.
     */
    @Test
    public void testWithLineNumber() {
        final int lineNumber = 1337;
        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(Integer.valueOf(lineNumber));

        assertThat(builder.withLineNumber(lineNumber).build().exclude("any_script", violation)).isTrue();
        assertThat(builder.withLineNumber(lineNumber + 1).build().exclude("a_script", violation)).isFalse();
    }

    /**
     * Test exclusion by script name.
     */
    @Test
    public void testWithScriptName() {
        final String scriptName = "a_script";
        final Violation violation = mock(Violation.class);

        assertThat(builder.withScriptName(scriptName).build().exclude(scriptName, violation)).isTrue();
        assertThat(builder.withScriptName(scriptName).build().exclude(StringUtils.reverse(scriptName), violation))
                .isFalse();
    }

    /**
     * Building with a {@code null} script name should fail.
     */
    @Test
    public void testWithScriptNameNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.withScriptName(null);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Test exclusion by variable name.
     */
    @Test
    public void testWithVariableName() {
        final String variableName = "a_variable";
        final VariableViolation violation = mock(VariableViolation.class);
        when(violation.getVariableName()).thenReturn(variableName);

        assertThat(builder.withVariableName(variableName).build().exclude("any_script", violation)).isTrue();
        assertThat(builder.withVariableName(StringUtils.reverse(variableName)).build().exclude("any_script", violation))
                .isFalse();
    }

    /**
     * Building a chain with a {@code null} variable name should fail.
     */
    @Test
    public void testWithVariableNameNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.withVariableName(null);
        });
        assertThat(e.getMessage()).isEqualTo("Variable name cannot be null.");
    }

    /**
     * Test exclusion by subroutine name.
     */
    @Test
    public void testWithSubroutineName() {
        final String subroutineName = "a_subroutine";
        final SubroutineViolation violation = mock(SubroutineViolation.class);
        when(violation.getSubroutineName()).thenReturn(subroutineName);

        assertThat(builder.withSubroutineName(subroutineName).build().exclude("any_script", violation)).isTrue();
        assertThat(builder.withSubroutineName(StringUtils.reverse(subroutineName)).build().exclude("any_script",
                violation)).isFalse();
    }

    /**
     * Building a chain with a {@code null} subroutine name should fail.
     */
    @Test
    public void testWithSubroutineNameNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.withSubroutineName(null);
        });
        assertThat(e.getMessage()).isEqualTo("Subroutine name cannot be null.");
    }

    /**
     * Test exclusion by violation ID.
     */
    @Test
    public void testWithViolationId() {
        final String violationId = "a.violation";
        final Violation violation = mock(Violation.class);
        when(violation.getViolationId()).thenReturn(new ViolationId("a", "violation"));

        assertThat(builder.withViolationId(violationId).build().exclude("any_script", violation)).isTrue();
        assertThat(builder.withViolationId(StringUtils.reverse(violationId)).build().exclude("any_script", violation))
                .isFalse();
    }

    /**
     * Building a filter chain with a {@code null} violation ID should fail.
     */
    @Test
    public void testWithViolationIdNullId() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.withViolationId(null);
        });
        assertThat(e.getMessage()).isEqualTo("Violation ID cannot be null.");
    }

    /**
     * Building with an unqualified violation ID should fail.
     */
    @Test
    public void testWithViolationIdUnqualifiedId() {
        final String unqualified = "unqualifiedName";
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.withViolationId(unqualified);
        });
        assertThat(e.getMessage()).isEqualTo("Violation ID must be fully qualified name: " + unqualified);
    }
}
