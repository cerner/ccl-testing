package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link ScriptDocumentation}.
 *
 * @author Joshua Hyde
 */
public class ScriptDocumentationTest extends AbstractBeanUnitTest<ScriptDocumentation> {
    private final String description = "i am the description";
    private final Integer boundTransaction = Integer.valueOf(2);
    @Mock
    private ScriptArgument argument;
    private List<ScriptArgument> arguments;
    private ScriptDocumentation documentation;

    /** Set up the documentation for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        arguments = Collections.singletonList(argument);
        documentation = new ScriptDocumentation(description, boundTransaction, arguments);
    }

    /** Test the construction of a documentation-less documentation object. */
    @Test
    public void testConstructNoDocumentation() {
        final ScriptDocumentation undocumented = new ScriptDocumentation();
        assertThat(undocumented.getBoundTransaction()).isNull();
        assertThat(undocumented.getDescription()).isEmpty();
        assertThat(undocumented.getScriptArguments()).isEmpty();
    }

    /**
     * A documentation object with a bound transaction should be inequal to a documentation without a bound transaction.
     */
    @Test
    public void testEqualsBoundToNoBoundTransaction() {
        final ScriptDocumentation unbound = new ScriptDocumentation(description, null, arguments);
        assertThat(documentation).isNotEqualTo(unbound);
        assertThat(unbound).isNotEqualTo(documentation);
    }

    /** Two documentation objects with different arguments should be inequal. */
    @Test
    public void testEqualsDifferentArguments() {
        final ScriptDocumentation other = new ScriptDocumentation(description, boundTransaction,
                Collections.<ScriptArgument> emptyList());
        assertThat(documentation).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(documentation);
    }

    /** Two documentation objects with different bound transactions should be inequal. */
    @Test
    public void testEqualsDifferentBoundTransaction() {
        final ScriptDocumentation other = new ScriptDocumentation(description, boundTransaction + 1, arguments);
        assertThat(documentation).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(documentation);
    }

    /** Two documentation objects of different descriptions should be inequal. */
    @Test
    public void testEqualsDifferentDescription() {
        final ScriptDocumentation other = new ScriptDocumentation(StringUtils.reverse(description), boundTransaction,
                arguments);
        assertThat(documentation).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(documentation);
    }

    /**
     * Two documentation objects with no bound transactions (and all other equal properties) should be equal.
     */
    @Test
    public void testEqualsNoBoundTransaction() {
        final ScriptDocumentation first = new ScriptDocumentation(description, null, arguments);
        final ScriptDocumentation second = new ScriptDocumentation(description, null, arguments);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /** Test the retrieval of the bound transaction. */
    @Test
    public void testGetBoundTransaction() {
        assertThat(documentation.getBoundTransaction()).isEqualTo(boundTransaction);
    }

    /** Test the retrieval of the description. */
    @Test
    public void testGetDescription() {
        assertThat(documentation.getDescription()).isEqualTo(description);
    }

    /** Test the retrieval of the script arguments. */
    @Test
    public void testGetScriptArguments() {
        assertThat(documentation.getScriptArguments()).isEqualTo(arguments);
    }

    @Override
    protected ScriptDocumentation getBean() {
        return documentation;
    }

    @Override
    protected ScriptDocumentation newBeanFrom(final ScriptDocumentation otherBean) {
        return new ScriptDocumentation(otherBean.getDescription(), otherBean.getBoundTransaction(),
                otherBean.getScriptArguments());
    }
}
