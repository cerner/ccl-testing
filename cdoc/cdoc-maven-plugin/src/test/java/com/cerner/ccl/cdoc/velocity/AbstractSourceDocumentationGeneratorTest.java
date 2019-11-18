package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.cdoc.velocity.structure.RecordStructureFormatter;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.data.record.RecordStructure;

/**
 * Unit tests for {@link AbstractSourceDocumentationGenerator}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AbstractSourceDocumentationGenerator.class, RecordStructureFormatter.class, URL.class,
        VelocityContext.class })
public class AbstractSourceDocumentationGeneratorTest {
    @Mock
    private Writer writer;
    @Mock
    private File cssDirectory;
    @Mock
    private VelocityEngine engine;
    @Mock
    private ScriptExecutionDetails executionDetails;
    @Mock
    private Navigation backNav;
    private ConcreteGenerator generator;

    /**
     * Set up the generator for each test.
     */
    @Before
    public void setUp() {
        generator = new ConcreteGenerator(writer, cssDirectory, executionDetails, engine, backNav);
    }

    /**
     * Construction with a {@code null} {@link Navigation} object for the back navigation should fail.
     */
    @Test
    public void testConstructNullBackNavigation() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteGenerator(writer, cssDirectory, executionDetails, engine, null);
        });
        assertThat(e.getMessage()).isEqualTo("Back navigation cannot be null.");
    }

    /**
     * Construction with a {@code null} CSS directory should fail.
     */
    @Test
    public void testConstructNullCssDirectory() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteGenerator(writer, null, executionDetails, engine, backNav);
        });
        assertThat(e.getMessage()).isEqualTo("CSS directory cannot be null.");
    }

    /**
     * Construction with a {@code null} {@link ScriptExecutionDetails} should fail.
     */
    @Test
    public void testConstructNullScriptExecutionDetails() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteGenerator(writer, cssDirectory, null, engine, backNav);
        });
        assertThat(e.getMessage()).isEqualTo("Script execution details cannot be null.");
    }

    /**
     * Construction with a {@code null} {@link Writer} should fail.
     */
    @Test
    public void testConstructNullWriter() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteGenerator(null, cssDirectory, executionDetails, engine, backNav);
        });
        assertThat(e.getMessage()).isEqualTo("Writer cannot be null.");
    }

    /**
     * Test the generation of documentation.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGenerate() throws Exception {
        final VelocityContext context = mock(VelocityContext.class);
        whenNew(VelocityContext.class).withNoArguments().thenReturn(context);

        final RecordStructure request = mock(RecordStructure.class);
        when(request.getStructureType()).thenReturn(InterfaceStructureType.REQUEST);
        final RecordStructure reply = mock(RecordStructure.class);
        when(reply.getStructureType()).thenReturn(InterfaceStructureType.REPLY);
        final RecordStructure neither = mock(RecordStructure.class);
        generator.setRecordStructure(Arrays.asList(request, reply, neither));

        final String formattedRequest = "formatted request";
        final String formattedReply = "formatted reply";
        final RecordStructureFormatter structureFormatter = mock(RecordStructureFormatter.class);
        when(structureFormatter.format(request)).thenReturn(formattedRequest);
        when(structureFormatter.format(reply)).thenReturn(formattedReply);
        whenNew(RecordStructureFormatter.class).withArguments(engine).thenReturn(structureFormatter);

        final Template template = mock(Template.class);
        when(engine.getTemplate("/velocity/source-doc.vm", "utf-8")).thenReturn(template);

        final Object object = new Object();
        generator.setObject(object);
        generator.setObjectFilename("object-filename");
        generator.setObjectName("object-name");

        final String cssUrlExternalForm = "i/am/the/external/form/of/the/CSS/directory/URL";
        URI uri = new URI("file:///" + cssUrlExternalForm);
        when(cssDirectory.toURI()).thenReturn(uri);

        generator.generate();

        verify(context).put("object", object);
        verify(context).put("objectName", "object-name");
        verify(context).put("objectFilename", "object-filename");
        verify(context).put("cssDirectory", "file:/" + cssUrlExternalForm);
        verify(context).put("executionDetails", executionDetails);
        verify(context).put("backNavigation", backNav);

        verify(context).put("requestDefinition", formattedRequest);
        verify(context).put("requestRecordStructure", request);
        verify(context).put("replyDefinition", formattedReply);
        verify(context).put("replyRecordStructure", reply);

        template.merge(context, writer);

        verify(structureFormatter, never()).format(neither);
    }

    /**
     * Concrete generator for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteGenerator extends AbstractSourceDocumentationGenerator<Object> {
        private final VelocityEngine engine;
        private Object object;
        private String objectFilename;
        private String objectName;
        private List<RecordStructure> recordStructures;

        /**
         * Create a generator.
         *
         * @param writer
         *            A {@link Writer}.
         * @param cssDirectory
         *            A {@link File}.
         * @param details
         *            A {@link ScriptExecutionDetails}.
         * @param engine
         *            A {@link VelocityEngine}.
         * @param backNavigation
         *            A {@link Navigation}.
         */
        public ConcreteGenerator(final Writer writer, final File cssDirectory, final ScriptExecutionDetails details,
                final VelocityEngine engine, final Navigation backNavigation) {
            super(writer, cssDirectory, details, backNavigation);

            this.engine = engine;
        }

        /**
         * Set the object for which documentation is to be generated.
         *
         * @param object
         *            The object for which documentation is to be generated.
         */
        public void setObject(final Object object) {
            this.object = object;
        }

        /**
         * Set the filename of the object.
         *
         * @param objectFilename
         *            The filename of the object.
         */
        public void setObjectFilename(final String objectFilename) {
            this.objectFilename = objectFilename;
        }

        /**
         * Set the object name.
         *
         * @param objectName
         *            The object name.
         */
        public void setObjectName(final String objectName) {
            this.objectName = objectName;
        }

        /**
         * Set the record structures to be returned by this generator.
         *
         * @param recordStructures
         *            A {@link List} of {@link RecordStructure} objects to be returned by this generator.
         */
        public void setRecordStructure(final List<RecordStructure> recordStructures) {
            this.recordStructures = recordStructures;
        }

        @Override
        protected VelocityEngine getEngine() {
            return engine;
        }

        @Override
        protected Object getObject() {
            return object;
        }

        @Override
        protected String getObjectFilename() {
            return objectFilename;
        }

        @Override
        protected String getObjectName() {
            return objectName;
        }

        @Override
        protected List<RecordStructure> getRecordStructures() {
            return recordStructures;
        }
    }
}
