package com.cerner.ccl.parser.text.documentation;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;

/**
 * Unit tests for {@link RecordStructureDocumentation}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureDocumentationTest extends AbstractBeanUnitTest<RecordStructureDocumentation> {
    private final String description = "i am the description";
    private final InterfaceStructureType structureType = InterfaceStructureType.REPLY;
    @Mock
    private Field field;
    private List<Field> fields;
    private RecordStructureDocumentation doc;

    /**
     * Set up the documentation object for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fields = Collections.singletonList(field);
        doc = new RecordStructureDocumentation(description, structureType, fields);
    }

    /**
     * Two structures of different structure types should be inequal.
     */
    @Test
    public void testEqualsDifferentStructureType() {
        final RecordStructureDocumentation other = new RecordStructureDocumentation(description,
                InterfaceStructureType.REQUEST, fields);
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * Two structures with different fields should be inequal.
     */
    @Test
    public void testEqualsDifferentFields() {
        final RecordStructureDocumentation other = new RecordStructureDocumentation(description, structureType,
                Collections.<Field> emptyList());
        assertThat(doc).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(doc);
    }

    /**
     * A record structure with a non-{@code null} structure type should not be equal to one with a {@code null}
     * structure type.
     */
    @Test
    public void testEqualsNullStructureType() {
        final RecordStructureDocumentation nullType = new RecordStructureDocumentation(description, null, fields);
        assertThat(doc).isNotEqualTo(nullType);
        assertThat(nullType).isNotEqualTo(doc);

        // Make sure that the null type is accounted for in the hash code
        assertThat(doc.hashCode()).isNotEqualTo(nullType.hashCode());
    }

    /**
     * Constructing a documentation object with {@code null} fields should simply store an empty list, internally.
     */
    @Test
    public void testConstructNullFields() {
        assertThat(new RecordStructureDocumentation(description, structureType, null).getFields()).isEmpty();
    }

    /**
     * Test the retrieval of the fields.
     */
    @Test
    public void testGetFields() {
        assertThat(doc.getFields()).isEqualTo(fields);
    }

    /**
     * Test the retrieval of the structure type.
     */
    @Test
    public void testGetStructureType() {
        assertThat(doc.getStructureType()).isEqualTo(structureType);
    }

    @Override
    protected RecordStructureDocumentation getBean() {
        return doc;
    }

    @Override
    protected RecordStructureDocumentation newBeanFrom(final RecordStructureDocumentation otherBean) {
        return new RecordStructureDocumentation(otherBean.getDescription(), otherBean.getStructureType(),
                otherBean.getFields());
    }

}
