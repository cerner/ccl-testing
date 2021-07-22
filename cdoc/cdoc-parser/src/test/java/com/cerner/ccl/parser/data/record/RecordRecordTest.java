package com.cerner.ccl.parser.data.record;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link RecordRecord}.
 *
 * @author Joshua Hyde
 */
public class RecordRecordTest extends AbstractBeanUnitTest<RecordRecord> {
    private final String name = "record_name";
    private final String description = "a description";
    @Mock
    private RecordStructureMember child;
    private List<RecordStructureMember> children;
    private RecordRecord record;

    /** Set up the record for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        children = Collections.singletonList(child);
        record = new RecordRecord(name, 1, description, children);
    }

    @Override
    protected RecordRecord getBean() {
        return record;
    }

    @Override
    protected RecordRecord newBeanFrom(final RecordRecord otherBean) {
        return new RecordRecord(otherBean.getName(), otherBean.getLevel(), otherBean.getDescription(),
                otherBean.getChildren());
    }
}
