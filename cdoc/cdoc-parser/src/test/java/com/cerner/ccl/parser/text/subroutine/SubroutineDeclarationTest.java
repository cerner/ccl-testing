package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.data.DataTyped;

/**
 * Unit tests for {@link SubroutineDeclaration}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineDeclarationTest {
    private final String subroutineName = "i am the subroutine name";
    private final String arg1Name = "arg1.Name";
    private final String arg2Name = "arg2.Name";
    @Mock
    private SubroutineArgumentDeclaration arg1;
    @Mock
    private SubroutineArgumentDeclaration arg2;
    @Mock
    private DataTyped returnType;
    private SubroutineDeclaration declaration;

    /**
     * Set up the declaration for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(arg1.getName()).thenReturn(arg1Name);
        when(arg2.getName()).thenReturn(arg2Name);

        declaration = new SubroutineDeclaration(subroutineName, returnType, Arrays.asList(arg1, arg2));
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineDeclaration(null, returnType, Collections.<SubroutineArgumentDeclaration> emptyList());
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * Construction with a {@code null} collection of subroutine arguments should fail.
     */
    @Test
    public void testConstructNullArguments() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineDeclaration(subroutineName, returnType, null);
        });
        assertThat(e.getMessage()).isEqualTo("Arguments cannot be null.");
    }

    /**
     * Test the retrieval of arguments.
     */
    @Test
    public void testGetArguments() {
        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();
        // Test case-insensitivity of retrieval by name
        assertThat(arguments.get(0)).isEqualTo(arg1);
        assertThat(arguments.get(1)).isEqualTo(arg2);
    }

    /**
     * Test the retrieval of the subroutine name.
     */
    @Test
    public void testGetName() {
        assertThat(declaration.getName()).isEqualTo(subroutineName);
    }

    /**
     * Test that the return type can be retrieved.
     */
    @Test
    public void testGetReturnType() {
        assertThat(declaration.getReturnType()).isEqualTo(returnType);
    }

}
