package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.StructureMember;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link StructureTest}.
 *
 * @author Joshua Hyde
 */
@SuppressWarnings("unused")
public class StructureTest extends AbstractBeanUnitTest<Structure> {
    private final String structureName = "structure_name";
    @Mock
    private StructureMember member;
    private List<StructureMember> members;
    private Structure structure;

    /** Set up the structure for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        members = Collections.singletonList(member);
        structure = new Structure(structureName, members);
    }

    /** Constructing with {@code null} members should fail. */
    @Test
    public void testConstructNullMembers() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new Structure(structureName, null);
        });
        assertThat(e.getMessage()).isEqualTo("Members cannot be null.");
    }

    /** Constructing with a {@code null} name should fail. */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new Structure(null, members);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /** Two structures with different members should be inequal. */
    @Test
    public void testEqualsDifferentMembers() {
        final Structure other = new Structure(structureName, Collections.singletonList(mock(StructureMember.class)));
        assertThat(structure).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(structure);
    }

    /** Two structures with different names should be inequal. */
    @Test
    public void testEqualsDifferentName() {
        final Structure other = new Structure(StringUtils.reverse(structureName), members);
        assertThat(structure).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(structure);
    }

    /** Test the retrieval of the name. */
    @Test
    public void testGetName() {
        assertThat(structure.getName()).isEqualTo(structureName);
    }

    /** Test the retrieval of a member. */
    @Test
    public void testGetRootLevelMember() {
        assertThat(structure.<StructureMember> getRootLevelMember(0)).isEqualTo(member);
    }

    /** Test the retrieval of the count of members. */
    @Test
    public void testGetRootLevelMemberCount() {
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(members.size());
    }

    @Override
    protected Structure getBean() {
        return structure;
    }

    @Override
    protected Structure newBeanFrom(final Structure otherBean) {
        return new Structure(otherBean.getName(), otherBean.getRootLevelMembers());
    }
}
