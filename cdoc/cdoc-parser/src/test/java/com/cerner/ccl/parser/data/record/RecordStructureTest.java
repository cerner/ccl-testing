package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link RecordStructure}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class RecordStructureTest extends AbstractBeanUnitTest<RecordStructure> {
    private final String name = "record_name";
    private final String description = "i am the description";
    private final InterfaceStructureType structureType = InterfaceStructureType.REPLY;
    @Mock
    private RecordStructureMember member;
    private List<RecordStructureMember> members;
    private RecordStructure recordStructure;

    /**
     * Set up the record structure for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        members = Collections.singletonList(member);
        recordStructure = new RecordStructure(name, members, description, structureType);
    }

    /**
     * Construction with a {@code null} list of members should fail.
     */
    @Test
    public void testConstructNullMembers() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new RecordStructure(name, null);
        });
        assertThat(e.getMessage()).isEqualTo("Members cannot be null.");
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new RecordStructure(null, members);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * Inequality should not be driven by any of the following:
     * <ul>
     * <li>{@link RecordStructure#getDescription() description}</li>
     * <li>{@link RecordStructure#getStructureType() structure type}</li>
     * </ul>
     */
    @Test
    public void testEqualsDifferentDocumentation() {
        final RecordStructure other = new RecordStructure(name, members, StringUtils.reverse(description), null);
        assertThat(recordStructure).isEqualTo(other);
        assertThat(other).isEqualTo(recordStructure);
        assertThat(other.hashCode()).isEqualTo(recordStructure.hashCode());
    }

    /**
     * Two record structures by different names should be inequal (unless they are merely different-cased versions of
     * each other; for that, see {@link #testEqualsNameCaseInsensitive()}.
     */
    @Test
    public void testEqualsDifferentName() {
        final RecordStructure other = new RecordStructure(StringUtils.reverse(name), members, description,
                recordStructure.getStructureType());
        assertThat(recordStructure).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(recordStructure);
    }

    /**
     * If two record structures merely have differently-cased versions of the same name, they should still be equal.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final RecordStructure other = new RecordStructure(StringUtils.swapCase(name), members);
        assertThat(recordStructure).isEqualTo(other);
        assertThat(other).isEqualTo(recordStructure);
        assertThat(recordStructure.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * If two record structures have different members, then they should be inequal.
     */
    @Test
    public void testEqualsDifferentMembers() {
        final RecordStructure other = new RecordStructure(name,
                Collections.singletonList(mock(RecordStructureMember.class)));
        assertThat(recordStructure).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(recordStructure);
    }

    /**
     * Test the retrieval of the description of a record structure.
     */
    @Test
    public void testGetDescription() {
        assertThat(recordStructure.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of the name of the record structure.
     */
    @Test
    public void testGetName() {
        assertThat(recordStructure.getName()).isEqualTo(name);
    }

    /**
     * Test the retrieval of members of the record structure.
     */
    @Test
    public void testGetRootLevelMember() {
        final RecordStructureField field = mock(RecordStructureField.class);
        final AbstractParentRecordStructureMember list = mock(AbstractParentRecordStructureMember.class);
        final RecordStructure toRetrieve = new RecordStructure(name, Arrays.asList(field, list));
        assertThat(toRetrieve.<RecordStructureField> getRootLevelMember(0)).isEqualTo(field);
        assertThat(toRetrieve.<RecordStructureField> getRootLevelMember(1)).isEqualTo(list);
    }

    /**
     * Test the retrieval of members from the record structure.
     */
    @Test
    public void testGetRootLevelMembers() {
        assertThat(recordStructure.getRootLevelMembers()).isEqualTo(members);
    }

    /**
     * Test the retrieval of the number of record structure members.
     */
    @Test
    public void testGetRootLevelMemberCount() {
        assertThat(recordStructure.getRootLevelMemberCount()).isEqualTo(members.size());
    }

    /**
     * Test the retrieval of the structure type.
     */
    @Test
    public void testGetStructureType() {
        assertThat(recordStructure.getStructureType()).isEqualTo(structureType);
    }

    @Override
    protected RecordStructure getBean() {
        return recordStructure;
    }

    @Override
    protected RecordStructure newBeanFrom(final RecordStructure otherBean) {
        return new RecordStructure(otherBean.getName(), otherBean.getRootLevelMembers(), otherBean.getDescription(),
                otherBean.getStructureType());
    }

}
