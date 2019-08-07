package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;
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
 * Unit tests for {@link AbstractParentRecordStructureMember}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class AbstractParentRecordStructureMemberTest extends AbstractBeanUnitTest<AbstractParentRecordStructureMember> {
    private final String name = "list_name";
    private final String description = "description";
    private final int level = 23;
    @Mock
    private RecordStructureMember child;
    private List<RecordStructureMember> children;
    private AbstractParentRecordStructureMember list;

    /**
     * Set up the list for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        children = Collections.singletonList(child);
        list = new ConcreteStructure(name, level, description, children);
    }

    /**
     * Construction with a {@code null} list of children should fail.
     */
    @Test
    public void testConstructNullChildren() {
        expect(IllegalArgumentException.class);
        expect("Children members cannot be null.");
        new ConcreteStructure(name, 1, "description", null);
    }

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        expect(IllegalArgumentException.class);
        expect("Name cannot be null.");
        new ConcreteStructure(null, 1, "description", children);
    }

    /**
     * Construction of the member with a 0 level should fail.
     */
    @Test
    public void testConstructZeroLevel() {
        expect(IllegalArgumentException.class);
        expect("Level cannot be less than 1: " + Integer.toString(0));
        new ConcreteStructure(name, 0, description, children);
    }

    /**
     * The description should not be considered when determining inequality.
     */
    @Test
    public void testEqualsDifferentDescription() {
        final AbstractParentRecordStructureMember other = new ConcreteStructure(name, 1,
                StringUtils.reverse(description), children);
        assertThat(list).isEqualTo(other);
        assertThat(other).isEqualTo(list);
        assertThat(list.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Two lists with different children should not be equal.
     */
    @Test
    public void testEqualsDifferentChildren() {
        final AbstractParentRecordStructureMember other = new ConcreteStructure(name, 1, description,
                Collections.singletonList(mock(RecordStructureMember.class)));
        assertThat(other).isNotEqualTo(list);
        assertThat(list).isNotEqualTo(other);
    }

    /**
     * The comparison by name should be case-insensitive.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final AbstractParentRecordStructureMember other = new ConcreteStructure(StringUtils.swapCase(name), 1,
                description, children);
        assertThat(list).isEqualTo(other);
        assertThat(other).isEqualTo(list);
        assertThat(other.hashCode()).isEqualTo(list.hashCode());
    }

    /**
     * Test the retrieval of child members.
     */
    @Test
    public void testGetChildMember() {
        final RecordStructureField field = mock(RecordStructureField.class);
        final AbstractParentRecordStructureMember list = mock(AbstractParentRecordStructureMember.class);
        final AbstractParentRecordStructureMember parent = new ConcreteStructure(name, 1, description,
                Arrays.asList(field, list));

        assertThat(parent.getChildMemberCount()).isEqualTo(2);
        assertThat(parent.<RecordStructureField> getChildMember(0)).isEqualTo(field);
        assertThat(parent.<RecordStructureField> getChildMember(1)).isEqualTo(list);
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(list.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of the level.
     */
    @Test
    public void testGetLevel() {
        assertThat(list.getLevel()).isEqualTo(level);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(list.getName()).isEqualTo(name);
    }

    @Override
    protected AbstractParentRecordStructureMember getBean() {
        return list;
    }

    @Override
    protected AbstractParentRecordStructureMember newBeanFrom(final AbstractParentRecordStructureMember otherBean) {
        return new ConcreteStructure(otherBean.getName(), otherBean.getLevel(), otherBean.getDescription(),
                otherBean.getChildren());
    }

    /**
     * Concrete implementation of {@link AbstractParentRecordStructureMember} to aid in testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteStructure extends AbstractParentRecordStructureMember {
        /**
         * Create a structure.
         *
         * @param name
         *            The name.
         * @param level
         *            The level.
         * @param description
         *            The description.
         * @param children
         *            The children.
         */
        public ConcreteStructure(final String name, final int level, final String description,
                final List<? extends RecordStructureMember> children) {
            super(name, level, description, children);
        }

    }

}
