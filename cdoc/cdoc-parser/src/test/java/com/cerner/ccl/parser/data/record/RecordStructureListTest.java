package com.cerner.ccl.parser.data.record;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link RecordStructureList}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureListTest extends AbstractBeanUnitTest<RecordStructureList> {
    private final String name = "list_name";
    private final String description = "a description";
    @Mock
    private RecordStructureMember child;
    private List<RecordStructureMember> children;
    private RecordStructureList list;

    /**
     * Set up the list for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        children = Collections.singletonList(child);
        list = new RecordStructureList(name, 1, description, children);
    }

    @Override
    protected RecordStructureList getBean() {
        return list;
    }

    @Override
    protected RecordStructureList newBeanFrom(final RecordStructureList otherBean) {
        return new RecordStructureList(otherBean.getName(), otherBean.getLevel(), otherBean.getDescription(),
                otherBean.getChildren());
    }

}
