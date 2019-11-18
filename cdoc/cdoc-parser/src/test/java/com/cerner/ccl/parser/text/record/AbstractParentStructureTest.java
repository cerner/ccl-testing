package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * Unit tests for {@link AbstractParentStructure}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class AbstractParentStructureTest extends AbstractBeanUnitTest<AbstractParentStructure> {
    private final String name = "list_name";
    private final int level = 7483;
    private final AbstractParentStructure list = new ConcreteStructure(name, level);

    /**
     * The addition of a {@code null} child member should fail.
     */
    @Test
    public void testAddChildMemberNullChild() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            list.addChildMember(null);
        });
        assertThat(e.getMessage()).isEqualTo("Child cannot be null.");
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteStructure(null, level);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * Construction with a level less than 1 should fail.
     */
    @Test
    public void testConstructZeroLevel() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteStructure(name, 0);
        });
        assertThat(e.getMessage()).isEqualTo("Level cannot be less than 1: " + Integer.toString(0));
    }

    /**
     * Two lists with different children should be inequal.
     */
    @Test
    public void testEqualsDifferentChildren() {
        final AbstractParentStructure other = new ConcreteStructure(name, level);
        other.addChildMember(mock(StructureMember.class));

        assertThat(other).isNotEqualTo(list);
        assertThat(list).isNotEqualTo(other);
    }

    /**
     * Two lists with different names should be inequal.
     */
    @Test
    public void testEqualsDifferentName() {
        final AbstractParentStructure other = new ConcreteStructure(StringUtils.reverse(name), level);
        assertThat(other).isNotEqualTo(list);
        assertThat(list).isNotEqualTo(other);
    }

    /**
     * Test the addition of a child member.
     */
    @Test
    public void testGetChildMember() {
        final StructureMember first = mock(StructureMember.class);
        final StructureMember second = mock(StructureMember.class);
        list.addChildMember(first);
        list.addChildMember(second);

        assertThat(list.getChildMemberCount()).isEqualTo(2);
        assertThat(list.<StructureMember> getChildMember(0)).isEqualTo(first);
        assertThat(list.<StructureMember> getChildMember(1)).isEqualTo(second);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(list.getName()).isEqualTo(name);
    }

    @Override
    protected AbstractParentStructure getBean() {
        return list;
    }

    @Override
    protected AbstractParentStructure newBeanFrom(final AbstractParentStructure otherBean) {
        return new ConcreteStructure(otherBean.getName(), otherBean.getLevel());
    }

    /**
     * Concrete implementation of {@link AbstractParentStructure} to help with testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteStructure extends AbstractParentStructure {
        /**
         * Create a concrete structure.
         *
         * @param name
         *            The name of the structure.
         * @param level
         *            The level of the member.
         */
        public ConcreteStructure(final String name, final int level) {
            super(name, level);
        }

    }
}
