package com.cerner.ccl.parser.subroutine;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.subroutine.Subroutine;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link Subroutine}.
 *
 * @author Joshua Hyde
 */
public class SubroutineTest extends AbstractBeanUnitTest<Subroutine> {
    private final String name = "subroutine_Name";
    private final String description = "this is a subroutine";
    private final String returnDescription = "the subroutine returns data";

    @Mock
    private DataTyped returnDataType;
    @Mock
    private SubroutineArgument argument;
    private List<SubroutineArgument> arguments;
    private Subroutine sub;

    /** Set up the subroutine for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        arguments = Collections.singletonList(argument);
        sub = new Subroutine(name, arguments, returnDataType, description, returnDescription);
    }

    /** Description should not be accounted for when determining equality. */
    @Test
    public void testEqualsDifferentDescription() {
        final Subroutine other = new Subroutine(name, arguments, returnDataType, StringUtils.reverse(description),
                returnDescription);
        assertThat(sub).isEqualTo(other);
        assertThat(other).isEqualTo(sub);
        assertThat(sub.hashCode()).isEqualTo(other.hashCode());
    }

    /** The return description should not impact determination of equality of subroutines. */
    @Test
    public void testEqualsDifferentReturnDescription() {
        final Subroutine other = new Subroutine(name, arguments, returnDataType, description,
                StringUtils.reverse(returnDescription));
        assertThat(sub).isEqualTo(other);
        assertThat(other).isEqualTo(sub);
        assertThat(sub.hashCode()).isEqualTo(other.hashCode());
    }

    /** Two subroutines with different names should be inequal. */
    @Test
    public void testEqualsDifferentName() {
        final Subroutine other = new Subroutine(StringUtils.reverse(name), arguments, returnDataType);
        assertThat(sub).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(sub);
    }

    /** Two subroutines with different arguments should be inequal. */
    @Test
    public void testEqualsDifferentArguments() {
        final Subroutine other = new Subroutine(name, Collections.<SubroutineArgument> emptyList(), returnDataType);
        assertThat(sub).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(sub);
    }

    /** Two subroutines with different return types should be inequal. */
    @Test
    public void testEqualsDifferentReturnType() {
        final Subroutine other = new Subroutine(name, arguments, mock(DataTyped.class));
        assertThat(sub).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(sub);
    }

    /**
     * Two subroutines that return void, when all other relevant properties match, should be equal.
     */
    @Test
    public void testEqualsReturnsVoid() {
        final Subroutine first = new Subroutine(name, arguments, null);
        final Subroutine second = new Subroutine(name, arguments, null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /** A subroutine that returns {@code void} should not be equal to one that does. */
    @Test
    public void testEqualsVoidVersusToVoid() {
        final Subroutine other = new Subroutine(name, arguments, null);
        assertThat(sub).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(sub);
    }

    /** Two subroutines by the same name, but different cases, should be equal. */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final Subroutine other = new Subroutine(StringUtils.swapCase(name), arguments, returnDataType);
        assertThat(sub).isEqualTo(other);
        assertThat(other).isEqualTo(sub);
        assertThat(sub.hashCode()).isEqualTo(other.hashCode());
    }

    @Override
    protected Subroutine getBean() {
        return sub;
    }

    @Override
    protected Subroutine newBeanFrom(final Subroutine otherBean) {
        return new Subroutine(otherBean.getName(), otherBean.getArguments(), otherBean.getReturnDataType(),
                otherBean.getDescription(), otherBean.getReturnDataDescription());
    }
}
