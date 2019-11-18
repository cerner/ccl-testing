package com.cerner.ccl.analysis.engine.j4ccl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.adders.ScriptCompilerAdder;

/**
 * Unit tests for {@link TranslatorCompiler}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CclExecutor.class, FileOutputStream.class, IOUtils.class, OutputStreamWriter.class,
        TranslatorCompiler.class })
public class TranslatorCompilerTest {
    /**
     * Test the execution of the compilation of the translation script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCompile() throws Exception {
        final File scriptFile = mock(File.class);
        final CclExecutor executor = mock(CclExecutor.class);

        final ScriptCompilerAdder<?> adder = mock(ScriptCompilerAdder.class);
        when(executor.addScriptCompiler(scriptFile)).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final TranslatorCompiler injected = new TranslatorCompiler() {
            @Override
            File copyTranslatorScript() {
                return scriptFile;
            }
        };

        injected.compile();
        verify(adder).commit();
        verify(executor).execute();
    }

    /**
     * Test the copying of the translator script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCopyTranslatorScript() throws Exception {
        final File tempFile = mock(File.class);
        final InputStream scriptStream = mock(InputStream.class);

        final TranslatorCompiler injected = new TranslatorCompiler() {
            @Override
            File createTemporaryFile() {
                return tempFile;
            }

            @Override
            InputStream getScriptStream() {
                return scriptStream;
            }
        };

        final FileOutputStream fileOut = mock(FileOutputStream.class);
        whenNew(FileOutputStream.class).withArguments(tempFile).thenReturn(fileOut);

        final OutputStreamWriter writer = mock(OutputStreamWriter.class);
        whenNew(OutputStreamWriter.class).withArguments(fileOut, Charset.forName("utf-8")).thenReturn(writer);

        mockStatic(IOUtils.class);

        assertThat(injected.copyTranslatorScript()).isEqualTo(tempFile);

        verifyStatic(IOUtils.class);
        IOUtils.copy(scriptStream, writer, Charset.forName("utf-8"));
    }
}
