package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link SubroutineDefinition}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineDefinitionTest {
    private final String subroutineName = "a name";
    private final List<String> argumentNames = Arrays.asList("a", "b", "c");
    private SubroutineDefinition definition;

    /**
     * Set up the subroutine definition for each test.
     */
    @Before
    public void setUp() {
        definition = new SubroutineDefinition(subroutineName, argumentNames);
    }

    /**
     * Construction with {@code null} argument names should fail.
     */
    @Test
    public void testConstructNullArgumentNames() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineDefinition(subroutineName, null);
        });
        assertThat(e.getMessage()).isEqualTo("Argument names cannot be null.");
    }

    /**
     * Construction with a {@code null} subroutine name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineDefinition(null, argumentNames);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * Two definitions with the same properties should be equal.
     */
    @Test
    public void testEquals() {
        final SubroutineDefinition other = new SubroutineDefinition(subroutineName,
                new ArrayList<String>(argumentNames));
        assertThat(definition).isEqualTo(other);
        assertThat(other).isEqualTo(definition);

        assertThat(other.hashCode()).isEqualTo(definition.hashCode());
    }

    /**
     * If the objects have different names, then they should not be equal.
     */
    @Test
    public void testEqualsDifferentName() {
        final SubroutineDefinition other = new SubroutineDefinition(StringUtils.reverse(subroutineName), argumentNames);
        assertThat(definition).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(definition);

        assertThat(definition.hashCode()).isNotEqualTo(other.hashCode());
    }

    /**
     * If the argument names are different, two definitions should not be equal.
     */
    @Test
    public void testEqualsDifferentArgumentNames() {
        final List<String> reversed = new ArrayList<String>(argumentNames);
        Collections.reverse(reversed);

        final SubroutineDefinition other = new SubroutineDefinition(subroutineName, reversed);
        assertThat(other).isNotEqualTo(definition);
        assertThat(definition).isNotEqualTo(other);

        assertThat(definition.hashCode()).isNotEqualTo(other.hashCode());
    }

    /**
     * If the object is not an instance of {@link SubroutineDefinition}, then they should not be equal.
     */
    @Test
    public void testEqualsNotSubroutineDefinition() {
        assertThat(definition).isNotEqualTo(new Object());
    }

    /**
     * A definition should not be equal to {@code null}.
     */
    @Test
    public void testEqualsNull() {
        assertThat(definition).isNotEqualTo(null);
    }

    /**
     * A definition should be equal to itself.
     */
    @Test
    public void testEqualsSelf() {
        assertThat(definition).isEqualTo(definition);
    }

    /**
     * Test the retrieval of the argument names.
     */
    @Test
    public void testGetArgumentNames() {
        assertThat(definition.getArgumentNames()).isEqualTo(argumentNames);
    }

    /**
     * Test the retrieval of the subroutine name.
     */
    @Test
    public void testGetName() {
        assertThat(definition.getName()).isEqualTo(subroutineName);
    }
}
