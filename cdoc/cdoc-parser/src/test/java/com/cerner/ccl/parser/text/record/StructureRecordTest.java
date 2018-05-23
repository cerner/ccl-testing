package com.cerner.ccl.parser.text.record;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * Unit tests for {@link StructureRecord}.
 *
 * @author Joshua Hyde
 *
 */

public class StructureRecordTest extends AbstractBeanUnitTest<StructureRecord> {
    private final String name = "record_name";
    private final StructureRecord record = new StructureRecord(name, 3);

    @Override
    protected StructureRecord getBean() {
        return record;
    }

    @Override
    protected StructureRecord newBeanFrom(final StructureRecord otherBean) {
        final StructureRecord newRecord = new StructureRecord(otherBean.getName(), otherBean.getLevel());
        for (final StructureMember member : otherBean.getChildMembers()) {
            newRecord.addChildMember(member);
        }

        return newRecord;
    }

}
