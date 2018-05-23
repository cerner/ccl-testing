package com.cerner.ccl.cdoc.velocity.structure;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.cdoc.AbstractUnitTest;
import com.cerner.ccl.parser.data.record.RecordInclude;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.data.record.RecordStructureList;

/**
 * Unit tests for {@link RecordStructureFormatter}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class RecordStructureFormatterTest extends AbstractUnitTest {
    @Mock
    private FieldFormatter fieldFormatter;
    @Mock
    private ParentalMemberFormatter listFormatter;
    @Mock
    private IncludeFormatter includeFormatter;
    private RecordStructureFormatter formatter;

    /**
     * Set up the formatter for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        formatter = new RecordStructureFormatter(fieldFormatter, listFormatter, includeFormatter);
    }

    /**
     * Construction with a {@code null} {@link FieldFormatter} should fail.
     */
    @Test
    public void testConstructNullFieldFormatter() {
        expect(IllegalArgumentException.class);
        expect("Field formatter cannot be null.");
        new RecordStructureFormatter(null, listFormatter, includeFormatter);
    }

    /**
     * Construction with a {@code null} include formatter should fail.
     */
    @Test
    public void testConstructNullIncludeFormatter() {
        expect(IllegalArgumentException.class);
        expect("Include formatter cannot be null.");
        new RecordStructureFormatter(fieldFormatter, listFormatter, null);
    }

    /**
     * Construction with a {@code null} {@link ParentalMemberFormatter} should fail.
     */
    @Test
    public void testConstructNullListFormatter() {
        expect(IllegalArgumentException.class);
        expect("List formatter cannot be null.");
        new RecordStructureFormatter(fieldFormatter, null, includeFormatter);
    }

    /**
     * Test the formatting of a record structure.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormat() throws Exception {
        final RecordStructureField field = mock(RecordStructureField.class);
        final String formattedField = "i am a formatted field";
        when(fieldFormatter.format(field)).thenReturn(formattedField);

        final RecordStructureList list = mock(RecordStructureList.class);
        final String formattedList = "i am a formatted list";
        when(listFormatter.format(list)).thenReturn(formattedList);

        final RecordInclude include = mock(RecordInclude.class);
        final String formattedInclude = "a formatted include";
        when(includeFormatter.format(include)).thenReturn(formattedInclude);

        final RecordStructure structure = mock(RecordStructure.class);
        when(structure.getRootLevelMemberCount()).thenReturn(Integer.valueOf(3));
        when(structure.getRootLevelMember(0)).thenReturn(field);
        when(structure.getRootLevelMember(1)).thenReturn(list);
        when(structure.getRootLevelMember(2)).thenReturn(include);

        assertThat(formatter.format(structure)).isEqualTo(formattedField + formattedList + formattedInclude);
    }

    /**
     * Formatting a {@code null} {@link RecordStructure} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormatNullStructure() throws Exception {
        expect(IllegalArgumentException.class);
        expect("Record structure cannot be null.");
        formatter.format(null);
    }
}
