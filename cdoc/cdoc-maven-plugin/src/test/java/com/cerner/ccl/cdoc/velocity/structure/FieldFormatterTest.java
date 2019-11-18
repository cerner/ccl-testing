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
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.data.record.RecordStructureField;

/**
 * Unit tests for {@link FieldFormatter}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { FieldFormatter.class, Random.class, StringWriter.class, VelocityContext.class })
public class FieldFormatterTest {
    @Mock
    private Template template;
    @Mock
    private Random random;
    private FieldFormatter formatter;

    /**
     * Set up the formatter for each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Before
    public void setUp() throws Exception {
        whenNew(Random.class).withNoArguments().thenReturn(random);

        final VelocityEngine engine = mock(VelocityEngine.class);
        when(engine.getTemplate("/velocity/record-structure-member-doc.vm", "utf-8")).thenReturn(template);
        formatter = new FieldFormatter(engine);
    }

    /**
     * Construction with a {@code null} {@link VelocityEngine} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testConstructNullEngine() throws Exception {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new FieldFormatter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Engine cannot be null.");
    }

    /**
     * Test formatting of a structure field.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormat() throws Exception {
        final VelocityContext context = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(context);

        final StringWriter writer = mock(StringWriter.class);
        final String formatted = "i am the formatted text";
        when(writer.toString()).thenReturn(formatted);
        whenNew(StringWriter.class).withNoArguments().thenReturn(writer);

        final int detailsNumber = 7483;
        when(random.nextInt()).thenReturn(Integer.valueOf(detailsNumber));

        final RecordStructureField member = mock(RecordStructureField.class);
        final String memberName = "member_name";
        when(member.getName()).thenReturn(memberName);

        assertThat(formatter.format(member)).isEqualTo(formatted);

        verify(context).put("member", member);
        verify(context).put("detailsDivName", memberName + detailsNumber);
        verify(template).merge(context, writer);
    }
}
