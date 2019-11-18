package com.cerner.ccl.cdoc.velocity.structure;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.data.record.RecordInclude;

/**
 * Unit tests for {@link IncludeFormatter}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { IncludeFormatter.class, StringWriter.class, VelocityContext.class })
public class IncludeFormatterTest {
    @Mock
    private VelocityEngine engine;
    @Mock
    private Template template;
    private IncludeFormatter formatter;

    /**
     * Set up the formatter for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        when(engine.getTemplate("/velocity/record-structure-include-doc.vm", "utf-8")).thenReturn(template);
        formatter = new IncludeFormatter(engine);
    }

    /**
     * Construction with a {@code null} engine should fail.
     *
     * @throws Exception
     *             If any unexpected errors occur during the test run.
     */
    @Test
    public void testConstructNullEngine() throws Exception {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new IncludeFormatter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Engine cannot be null.");
    }

    /**
     * Test the formatting for an include file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFormat() throws Exception {
        final VelocityContext context = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(context);

        final String formatted = "i am the formatted text";
        final StringWriter writer = mock(StringWriter.class);
        when(writer.toString()).thenReturn(formatted);
        whenNew(StringWriter.class).withNoArguments().thenReturn(writer);

        final RecordInclude include = mock(RecordInclude.class);

        assertThat(formatter.format(include)).isEqualTo(formatted);
        verify(context).put("member", include);
        verify(template).merge(context, writer);
    }
}
