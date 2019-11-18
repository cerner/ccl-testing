package com.cerner.ccl.cdoc.velocity.structure;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.StringWriter;
import java.util.Random;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.RecordStructureField;

/**
 * Unit tests of {@link ParentalMemberFormatter}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { ParentalMemberFormatter.class, Random.class, StringWriter.class, VelocityContext.class })
public class ParentalMemberFormatterTest {
    @Mock
    private FieldFormatter fieldFormatter;
    @Mock
    private Template template;
    @Mock
    private Random random;
    private ParentalMemberFormatter formatter;

    /**
     * Set up the formatter for each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Before
    public void setUp() throws Exception {
        whenNew(Random.class).withNoArguments().thenReturn(random);
        formatter = new ParentalMemberFormatter(fieldFormatter, template);
    }

    /**
     * Construction with a {@code null} field formatter should fail.
     */
    @Test
    public void testConstructNullFieldFormatter() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ParentalMemberFormatter(null, template);
        });
        assertThat(e.getMessage()).isEqualTo("Field formatter cannot be null.");
    }

    /**
     * Construction with a {@code null} template should fail.
     */
    @Test
    public void testConstructNullTemplate() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ParentalMemberFormatter(fieldFormatter, null);
        });
        assertThat(e.getMessage()).isEqualTo("Template cannot be null.");
    }

    /**
     * Test the formatting of a record structure with a child member that is a field.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormatChildMember() throws Exception {
        final int detailsNumber = 43;
        when(random.nextInt()).thenReturn(Integer.valueOf(detailsNumber));

        final RecordStructureField childMember = mock(RecordStructureField.class);
        final String childFormatted = "i am the formatted child of a parent";
        final String childName = "child_name";
        when(childMember.getName()).thenReturn(childName);
        when(fieldFormatter.format(childMember)).thenReturn(childFormatted);

        final AbstractParentRecordStructureMember parent = mock(AbstractParentRecordStructureMember.class);
        final String parentName = "parent";
        final String parentFormatted = "i am the formatted parent";
        when(parent.getChildMember(0)).thenReturn(childMember);
        when(parent.getChildMemberCount()).thenReturn(Integer.valueOf(1));
        when(parent.getName()).thenReturn(parentName);

        final StringWriter writer = mock(StringWriter.class);
        whenNew(StringWriter.class).withNoArguments().thenReturn(writer);
        when(writer.toString()).thenReturn(parentFormatted);

        final VelocityContext context = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(context);

        assertThat(formatter.format(parent)).isEqualTo(parentFormatted + childFormatted);

        verify(context).put("member", parent);
        verify(context).put("detailsDivName", parentName + detailsNumber);
        verify(template).merge(context, writer);
    }

    /**
     * Test formatting of a parental member with a child parental member.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormatChildParent() throws Exception {
        final int firstDetailsNumber = 23;
        final int secondDetailsNumber = 478;
        when(random.nextInt()).thenReturn(Integer.valueOf(firstDetailsNumber), Integer.valueOf(secondDetailsNumber));

        final AbstractParentRecordStructureMember childParent = mock(AbstractParentRecordStructureMember.class);
        final String childParentFormatted = "i am the formatted child of a parent";
        final String childParentName = "child_parent_name";
        when(childParent.getName()).thenReturn(childParentName);

        final AbstractParentRecordStructureMember parent = mock(AbstractParentRecordStructureMember.class);
        final String parentName = "parent";
        final String parentFormatted = "i am the formatted parent";
        when(parent.getChildMemberCount()).thenReturn(Integer.valueOf(1));
        when(parent.getChildMember(0)).thenReturn(childParent);
        when(parent.getName()).thenReturn(parentName);

        final StringWriter firstWriter = mock(StringWriter.class);
        when(firstWriter.toString()).thenReturn(parentFormatted);

        final StringWriter secondWriter = mock(StringWriter.class);
        when(secondWriter.toString()).thenReturn(childParentFormatted);
        whenNew(StringWriter.class).withNoArguments().thenReturn(firstWriter, secondWriter);

        final VelocityContext firstContext = mock(VelocityContext.class);
        final VelocityContext secondContext = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(firstContext, secondContext);

        assertThat(formatter.format(parent)).isEqualTo(parentFormatted + childParentFormatted);

        verify(firstContext).put("member", parent);
        verify(firstContext).put("detailsDivName", parentName + firstDetailsNumber);
        verify(template).merge(firstContext, firstWriter);

        verify(secondContext).put("member", childParent);
        verify(secondContext).put("detailsDivName", childParentName + secondDetailsNumber);
        verify(template).merge(secondContext, secondWriter);
    }
}
