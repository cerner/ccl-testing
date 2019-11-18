package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link FixedLengthStructureList}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class FixedLengthStructureListTest extends AbstractBeanUnitTest<FixedLengthStructureList> {
    private final String name = "list_name";
    private final int listSize = 23;
    private final FixedLengthStructureList list = new FixedLengthStructureList(name, 1, listSize);

    /**
     * Construction with a negative list size should fail.
     */
    @Test
    public void testConstructNegativeListSize() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new FixedLengthStructureList(name, 1, -1);
        });
        assertThat(e.getMessage()).isEqualTo("List size cannot be negative: " + Integer.toBinaryString(-1));
    }

    /**
     * Two lists with different list sizes must be inequal.
     */
    @Test
    public void testEqualsDifferentListSize() {
        final FixedLengthStructureList other = new FixedLengthStructureList(name, 1, listSize + 1);
        assertThat(list).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(list);
    }

    /**
     * Test the retrieval of the list size.
     */
    @Test
    public void testGetListSize() {
        assertThat(list.getListSize()).isEqualTo(listSize);
    }

    @Override
    protected FixedLengthStructureList getBean() {
        return list;
    }

    @Override
    protected FixedLengthStructureList newBeanFrom(final FixedLengthStructureList otherBean) {
        return new FixedLengthStructureList(otherBean.getName(), otherBean.getLevel(), otherBean.getListSize());
    }

}
