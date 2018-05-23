package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link FixedLengthRecordStructureList}.
 *
 * @author Joshua Hyde
 *
 */

public class FixedLengthRecordStructureListTest extends AbstractBeanUnitTest<FixedLengthRecordStructureList> {
    private final String name = "fixed_list";
    private final int listSize = 23;
    @Mock
    private RecordStructureMember member;
    private List<RecordStructureMember> members;
    private FixedLengthRecordStructureList list;

    /**
     * Set up the list for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        members = Collections.singletonList(member);
        list = new FixedLengthRecordStructureList(name, 1, members, listSize);
    }

    /**
     * Two lists of different static sizes should not be equal.
     */
    @Test
    public void testEqualsDifferentListSize() {
        final FixedLengthRecordStructureList other = new FixedLengthRecordStructureList(name, 1, members, listSize + 1);
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
    protected FixedLengthRecordStructureList getBean() {
        return list;
    }

    @Override
    protected FixedLengthRecordStructureList newBeanFrom(final FixedLengthRecordStructureList otherBean) {
        return new FixedLengthRecordStructureList(otherBean.getName(), otherBean.getLevel(), otherBean.getDescription(),
                otherBean.getChildren(), otherBean.getListSize());
    }
}
