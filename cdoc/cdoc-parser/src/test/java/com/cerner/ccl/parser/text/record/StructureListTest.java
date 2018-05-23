package com.cerner.ccl.parser.text.record;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * Unit tests for {@link StructureList}.
 *
 * @author Joshua Hyde
 *
 */

public class StructureListTest extends AbstractBeanUnitTest<StructureList> {
    private final String name = "list_name";
    private final StructureList list = new StructureList(name, 1);

    @Override
    protected StructureList getBean() {
        return list;
    }

    @Override
    protected StructureList newBeanFrom(final StructureList otherBean) {
        final StructureList newList = new StructureList(otherBean.getName(), otherBean.getLevel());
        for (final StructureMember member : otherBean.getChildMembers()) {
            newList.addChildMember(member);
        }
        return newList;
    }

}
