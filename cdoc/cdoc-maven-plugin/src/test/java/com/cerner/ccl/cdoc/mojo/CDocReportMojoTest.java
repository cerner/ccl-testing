package com.cerner.ccl.cdoc.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharSet;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.cdoc.mojo.CDocReportMojoTest.CssCopyRecordingMojo.CopyInvocation;
import com.cerner.ccl.cdoc.mojo.CDocReportMojoTest.ExecutionRecordingMojo.WriteInvocation;
import com.cerner.ccl.cdoc.mojo.data.Documentation;
import com.cerner.ccl.cdoc.mojo.data.ObjectType;
import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.script.ScriptExecutionDetailsParser;
import com.cerner.ccl.cdoc.velocity.IncludeDocGenerator;
import com.cerner.ccl.cdoc.velocity.ScriptDocGenerator;
import com.cerner.ccl.cdoc.velocity.SummaryGenerator;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.text.TextParser;

/**
 * Unit tests for {@link CDocReportMojo}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CDocReportMojo.class, CharSet.class, Documentation.class, File.class, FileOutputStream.class,
        FileUtils.class, IncludeDocGenerator.class, OutputStreamWriter.class, ScriptDocGenerator.class,
        SummaryGenerator.class })
public class CDocReportMojoTest {
    @Mock
    private TextParser textParser;
    @Mock
    private ScriptExecutionDetailsParser detailsParser;
    private CDocReportMojo mojo;

    /**
     * Set up the mojo for each test.
     */
    @Before
    public void setUp() {
        mojo = new CDocReportMojo(textParser, detailsParser);
    }

    /**
     * If at least one qualifying file is found, then the report should be generated.
     */
    @Test
    public void testCanGenerateReport() {
        assertThat(new CDocReportMojo(textParser, detailsParser) {
            @Override
            List<File> getFiles() {
                return Collections.singletonList(mock(File.class));
            }
        }.canGenerateReport()).isTrue();
    }

    /**
     * If no file are found, then no report can be generated.
     */
    @Test
    public void testCanGenerateReportNoFiles() {
        assertThat(new CDocReportMojo(textParser, detailsParser) {
            @Override
            List<File> getFiles() {
                return Collections.emptyList();
            }
        }.canGenerateReport()).isFalse();
    }

    /**
     * Construction with a {@code null} {@link ScriptExecutionDetailsParser} should fail.
     */
    @Test
    public void testConstructNullDetailsParser() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CDocReportMojo(textParser, null);
        });
        assertThat(e.getMessage()).isEqualTo("Script execution details parser cannot be null.");
    }

    /**
     * Construction with a {@code null} {@link TextParser} should fail.
     */
    @Test
    public void testConstructNullTextParser() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CDocReportMojo(null, detailsParser);
        });
        assertThat(e.getMessage()).isEqualTo("Text parser cannot be null.");
    }

    /**
     * Test the copying of a CSS file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCopyCss() throws Exception {
        final File tempDirectory = File.createTempFile("testCopyCss", null).getParentFile();
        mojo.copyCss(tempDirectory, "fake-css.css");

        final File cssFile = new File(tempDirectory, "fake-css.css");
        assertThat(cssFile).exists();
        try {
            assertThat(FileUtils.fileRead(cssFile, "utf-8")).isEqualTo("/* I am a fake CSS file */");
        } finally {
            FileUtils.forceDelete(cssFile);
        }
    }

    /**
     * Test the execution of the report.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteReport() throws Exception {
        final List<File> files = mock(List.class);
        final List<Documentation> documentation = mock(List.class);

        final File destinationDirectory = mock(File.class);
        final File cssDirectory = mock(File.class);
        when(cssDirectory.exists()).thenReturn(Boolean.TRUE);
        whenNew(File.class).withArguments(destinationDirectory, "css").thenReturn(cssDirectory);

        final ExecutionRecordingMojo testMojo = new ExecutionRecordingMojo(textParser, detailsParser);
        testMojo.setDestinationDirectory(destinationDirectory);
        testMojo.setFiles(files);
        testMojo.addDocumentation(files, documentation);
        testMojo.executeReport(Locale.getDefault());

        assertThat(testMojo.getObjectDocInvocations()).hasSize(1);
        final WriteInvocation objectInvocation = testMojo.getObjectDocInvocations().get(0);
        assertThat(objectInvocation.getCssDirectory()).isEqualTo(cssDirectory);
        assertThat(objectInvocation.getDocs()).isEqualTo(documentation);

        assertThat(testMojo.getSummaryInvocations()).hasSize(1);
        final WriteInvocation summaryInvocation = testMojo.getSummaryInvocations().get(0);
        assertThat(summaryInvocation.getCssDirectory()).isEqualTo(cssDirectory);
        assertThat(summaryInvocation.getDocs()).isEqualTo(documentation);

        assertThat(testMojo.getCopyInvocations()).hasSize(3);

        final CopyInvocation cdocCssInvocation = testMojo.getCopyInvocations().get(0);
        assertThat(cdocCssInvocation.getCssDirectory()).isEqualTo(cssDirectory);
        assertThat(cdocCssInvocation.getCssFilename()).isEqualTo("cdoc.css");

        final CopyInvocation scriptCssInvocation = testMojo.getCopyInvocations().get(1);
        assertThat(scriptCssInvocation.getCssDirectory()).isEqualTo(cssDirectory);
        assertThat(scriptCssInvocation.getCssFilename()).isEqualTo("script-doc.css");

        final CopyInvocation summaryCssInvocation = testMojo.getCopyInvocations().get(2);
        assertThat(summaryCssInvocation.getCssDirectory()).isEqualTo(cssDirectory);
        assertThat(summaryCssInvocation.getCssFilename()).isEqualTo("script-doc-summary.css");
    }

    /**
     * If the CSS directory indicates that it doesn't exist, then it should be created.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteReportMakeCssDirectory() throws Exception {
        mockStatic(FileUtils.class);

        final List<File> files = mock(List.class);
        final List<Documentation> documentation = mock(List.class);

        final File destinationDirectory = mock(File.class);
        final File cssDirectory = mock(File.class);
        when(cssDirectory.exists()).thenReturn(Boolean.FALSE);
        whenNew(File.class).withArguments(destinationDirectory, "css").thenReturn(cssDirectory);

        final ExecutionRecordingMojo testMojo = new ExecutionRecordingMojo(textParser, detailsParser);
        testMojo.setDestinationDirectory(destinationDirectory);
        testMojo.setFiles(files);
        testMojo.addDocumentation(files, documentation);
        testMojo.executeReport(Locale.getDefault());

        verifyStatic(FileUtils.class);
        FileUtils.forceMkdir(cssDirectory);
    }

    /**
     * Test the retrieval of the mojo description.
     */
    @Test
    public void testGetDescription() {
        assertThat(mojo.getDescription(Locale.getDefault()))
                .isEqualTo("A report of documentation surrounding CCL scripts.");
    }

    /**
     * Test the retrieval of the destination directory.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDestinationDirectory() throws Exception {
        final String outputAbsolutePath = "i am the absolute path";
        final File outputDirectory = mock(File.class);
        when(outputDirectory.getAbsolutePath()).thenReturn(outputAbsolutePath);
        mojo.outputDirectory = outputDirectory;

        final File destinationDirectory = mock(File.class);
        whenNew(File.class).withArguments(outputAbsolutePath, "cdoc-report").thenReturn(destinationDirectory);

        assertThat(mojo.getDestinationDirectory()).isEqualTo(destinationDirectory);
    }

    /**
     * Test the retrieval of files.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFiles() throws Exception {
        final File sourceFile = mock(File.class);
        final File sourceDirectory = mock(File.class);
        when(sourceDirectory.exists()).thenReturn(Boolean.TRUE);

        final File resourceFile = mock(File.class);
        final File resourcesDirectory = mock(File.class);
        when(resourcesDirectory.exists()).thenReturn(Boolean.TRUE);

        mockStatic(FileUtils.class);
        when(FileUtils.getFiles(sourceDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(sourceFile));
        when(FileUtils.getFiles(resourcesDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(resourceFile));

        mojo.sourceDirectory = sourceDirectory;
        mojo.resourcesDirectory = resourcesDirectory;
        assertThat(mojo.getFiles()).containsOnly(sourceFile, resourceFile);
    }

    /**
     * If the resources directory doesn't exist, then it should not be searched.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFilesNoResourcesDirectory() throws Exception {
        final File sourceFile = mock(File.class);
        final File sourceDirectory = mock(File.class);
        when(sourceDirectory.exists()).thenReturn(Boolean.TRUE);

        final File resourceFile = mock(File.class);
        final File resourcesDirectory = mock(File.class);
        when(resourcesDirectory.exists()).thenReturn(Boolean.FALSE);

        mockStatic(FileUtils.class);
        when(FileUtils.getFiles(sourceDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(sourceFile));
        when(FileUtils.getFiles(resourcesDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(resourceFile));

        mojo.sourceDirectory = sourceDirectory;
        mojo.resourcesDirectory = resourcesDirectory;
        assertThat(mojo.getFiles()).containsOnly(sourceFile);
    }

    /**
     * If the sources directory doesn't exist, then it should not be searched.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFilesNoSourcesDirectory() throws Exception {
        final File sourceFile = mock(File.class);
        final File sourceDirectory = mock(File.class);
        when(sourceDirectory.exists()).thenReturn(Boolean.FALSE);

        final File resourceFile = mock(File.class);
        final File resourcesDirectory = mock(File.class);
        when(resourcesDirectory.exists()).thenReturn(Boolean.TRUE);

        mockStatic(FileUtils.class);
        when(FileUtils.getFiles(sourceDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(sourceFile));
        when(FileUtils.getFiles(resourcesDirectory, "*.prg,*.inc,*.sub", ""))
                .thenReturn(Collections.singletonList(resourceFile));

        mojo.sourceDirectory = sourceDirectory;
        mojo.resourcesDirectory = resourcesDirectory;
        assertThat(mojo.getFiles()).containsOnly(resourceFile);
    }

    /**
     * Test the parsing of an include file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetIncludeFile() throws Exception {
        final String filename = "i am THE filename";
        final List<String> source = Collections.singletonList("i am the test data.");

        final IncludeFile includeFile = mock(IncludeFile.class);
        when(textParser.parseIncludeFile(filename.toLowerCase(Locale.US), source)).thenReturn(includeFile);
        assertThat(mojo.getIncludeFile(filename, source)).isEqualTo(includeFile);
    }

    /**
     * Test the retrieval of the mojo name.
     */
    @Test
    public void testGetName() {
        assertThat(mojo.getName(Locale.getDefault())).isEqualTo("CDoc");
    }

    /**
     * Test the retrieval of the output directory path.
     */
    @Test
    public void testGetOutputDirectory() {
        final String absolutePath = "i am the absolute path";
        final File outputDirectory = mock(File.class);
        when(outputDirectory.getAbsolutePath()).thenReturn(absolutePath);
        mojo.outputDirectory = outputDirectory;

        assertThat(mojo.getOutputDirectory()).isEqualTo(absolutePath);
    }

    /**
     * Test the return of the output encoding.
     */
    @Test
    public void testGetOutputEncoding() {
        final String outputEncoding = "i am the bestest encoding EVAR!!";

        mojo.outputEncoding = outputEncoding;
        assertThat(mojo.getOutputEncoding()).isEqualTo(outputEncoding);
    }

    /**
     * If the output encoding is not set, then UTF-8 should be returned.
     */
    @Test
    public void testGetOutputEncodingUnset() {
        mojo.outputEncoding = null;
        assertThat(mojo.getOutputEncoding()).isEqualTo("utf-8");
    }

    /**
     * Test the retrieval of the output name.
     */
    @Test
    public void testGetOutputName() {
        assertThat(mojo.getOutputName()).isEqualTo("cdoc-report");
    }

    /**
     * Test the retrieval of the Maven project.
     */
    @Test
    public void testGetProject() {
        final MavenProject project = mock(MavenProject.class);
        mojo.project = project;
        assertThat(mojo.getProject()).isEqualTo(project);
    }

    /**
     * Test the parsing of a CCL script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetScript() throws Exception {
        final List<String> source = Collections.singletonList("whoooooo test data!");
        final String filename = "you have FAILED ME for the last time.prg";

        final CclScript script = mock(CclScript.class);
        when(textParser.parseCclScript(filename.substring(0, filename.lastIndexOf('.')).toLowerCase(Locale.US), source))
                .thenReturn(script);
        assertThat(mojo.getScript(filename, source)).isEqualTo(script);
    }

    /**
     * Test the retrieval of the site renderer.
     */
    @Test
    public void testGetSiteRenderer() {
        final Renderer renderer = mock(Renderer.class);
        mojo.siteRenderer = renderer;
        assertThat(mojo.getSiteRenderer()).isEqualTo(renderer);
    }

    /**
     * Test the creation of a writer.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetWriter() throws Exception {
        final String testData = "whoa whoa whoa, lois, this is not my batman cup";
        final String filename = "testGetWriter.output";
        final File outputDirectory = File.createTempFile("testGetWriter", null).getParentFile();
        final File expectedFile = new File(outputDirectory, "/cdoc-report/" + filename);

        CDocReportMojo mojo = new CDocReportMojo();

        Field fieldOutputDirectory = mojo.getClass().getDeclaredField("outputDirectory");
        Field fieldOutputEncoding = mojo.getClass().getDeclaredField("outputEncoding");
        fieldOutputDirectory.setAccessible(true);
        fieldOutputEncoding.setAccessible(true);
        fieldOutputDirectory.set(mojo, outputDirectory);
        fieldOutputEncoding.set(mojo, "utf-8");

        final Documentation doc = mock(Documentation.class);
        when(doc.getDestinationFilename()).thenReturn(filename);

        try (Writer writer = mojo.getWriter(doc)) {
            IOUtils.write(testData.getBytes("utf-8"), writer, "utf-8");
        }

        assertThat(expectedFile).exists();
        try {
            assertThat(FileUtils.fileRead(expectedFile, "utf-8")).isEqualTo(testData);
        } finally {
            FileUtils.forceDelete(expectedFile);
        }
    }

    /**
     * The mojo should assert itself as an external report.
     */
    @Test
    public void testIsExternalReport() {
        assertThat(mojo.isExternalReport()).isTrue();
    }

    /**
     * Test the reading of a file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testReadSource() throws Exception {
        final String testData = "data, data, data!";
        final File outputFile = File.createTempFile("testReadSource", null);
        FileUtils.fileWrite(outputFile.getAbsolutePath(), "utf-8", testData);

        assertThat(mojo.readSource(outputFile)).containsOnly(testData);
    }

    /**
     * Test the conversion of files to {@link Documentation} objects.
     * <p>
     * The names of the objects are intentionally switched (docA = 2, docB = 1) so that it can be verified that they
     * were sorted by their names.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testToDocumentation() throws Exception {
        final File fileA = mock(File.class);
        final Documentation docA = mock(Documentation.class);
        when(docA.getObjectName()).thenReturn("2");
        whenNew(Documentation.class).withArguments(fileA).thenReturn(docA);

        final File fileB = mock(File.class);
        final Documentation docB = mock(Documentation.class);
        when(docB.getObjectName()).thenReturn("1");
        whenNew(Documentation.class).withArguments(fileB).thenReturn(docB);

        assertThat(mojo.toDocumentation(Arrays.asList(fileA, fileB))).containsExactly(docB, docA);
    }

    /**
     * Test the writing of documentation summary.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteDocumentationSummary() throws Exception {
        final String outputAbsolutePath = "i am absolutely the output path";
        final File outputDirectory = mock(File.class);
        when(outputDirectory.getAbsolutePath()).thenReturn(outputAbsolutePath);

        final SummaryGenerator generator = mock(SummaryGenerator.class);
        whenNew(SummaryGenerator.class).withNoArguments().thenReturn(generator);

        final File outputFile = mock(File.class);
        whenNew(File.class).withArguments(outputAbsolutePath, "cdoc-report.html").thenReturn(outputFile);

        final FileOutputStream fileOutputStream = mock(FileOutputStream.class);
        whenNew(FileOutputStream.class).withArguments(outputFile).thenReturn(fileOutputStream);

        final OutputStreamWriter writer = mock(OutputStreamWriter.class);
        whenNew(OutputStreamWriter.class).withArguments(fileOutputStream, Charset.forName("utf-8")).thenReturn(writer);

        final MavenProject project = mock(MavenProject.class);
        @SuppressWarnings("unchecked")
        final List<Documentation> docs = mock(List.class);
        final File cssDirectory = mock(File.class);

        final CssCopyRecordingMojo testMojo = new CssCopyRecordingMojo(textParser, detailsParser);

        Field fieldOutputDirectory = testMojo.getClass().getSuperclass().getDeclaredField("outputDirectory");
        Field fieldOutputEncoding = testMojo.getClass().getSuperclass().getDeclaredField("outputEncoding");
        Field fieldProject = testMojo.getClass().getSuperclass().getDeclaredField("project");
        fieldOutputDirectory.setAccessible(true);
        fieldOutputEncoding.setAccessible(true);
        fieldProject.setAccessible(true);
        fieldOutputDirectory.set(testMojo, outputDirectory);
        fieldOutputEncoding.set(testMojo, "utf-8");
        fieldProject.set(testMojo, project);

        testMojo.writeDocumentationSummary(cssDirectory, docs);

        verify(generator).generate(project, docs, cssDirectory, writer);
    }

    /**
     * Test the writing of documentation for an include file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteObjectDocumentationInc() throws Exception {
        final IncludeFile includeFile = mock(IncludeFile.class);
        final File cssDirectory = mock(File.class);
        final Writer writer = mock(Writer.class);

        @SuppressWarnings("unchecked")
        final List<String> source = mock(List.class);
        final String sourceFilename = "i am the source filename";
        final File sourceFile = mock(File.class);
        when(sourceFile.getName()).thenReturn(sourceFilename);

        final Documentation doc = mock(Documentation.class);
        when(doc.getObjectType()).thenReturn(ObjectType.INC);
        when(doc.getSourceFile()).thenReturn(sourceFile);

        final ParserInjectionMojo testMojo = new ParserInjectionMojo(textParser, detailsParser);
        testMojo.setIncludeFile(sourceFilename, includeFile);
        testMojo.setWriter(doc, writer);
        testMojo.setSource(sourceFile, source);

        final Navigation navigation = mock(Navigation.class);

        final ScriptExecutionDetails executionDetails = mock(ScriptExecutionDetails.class);
        when(detailsParser.getDetails(source)).thenReturn(executionDetails);

        final IncludeDocGenerator generator = mock(IncludeDocGenerator.class);
        whenNew(IncludeDocGenerator.class)
                .withArguments(includeFile, cssDirectory, executionDetails, writer, navigation).thenReturn(generator);

        testMojo.writeObjectDocumentation(cssDirectory, Collections.singletonList(doc), navigation);
        verify(generator).generate();
    }

    /**
     * Test the generation of documentation for a CCL script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteObjectDocumentationPrg() throws Exception {
        final CclScript cclScript = mock(CclScript.class);
        final File cssDirectory = mock(File.class);
        final Writer writer = mock(Writer.class);

        @SuppressWarnings("unchecked")
        final List<String> source = mock(List.class);
        final String sourceFilename = "i am the source filename";
        final File sourceFile = mock(File.class);
        when(sourceFile.getName()).thenReturn(sourceFilename);

        final Documentation doc = mock(Documentation.class);
        when(doc.getObjectType()).thenReturn(ObjectType.PRG);
        when(doc.getSourceFile()).thenReturn(sourceFile);

        final ParserInjectionMojo testMojo = new ParserInjectionMojo(textParser, detailsParser);
        testMojo.setScript(sourceFilename, cclScript);
        testMojo.setWriter(doc, writer);
        testMojo.setSource(sourceFile, source);

        final Navigation navigation = mock(Navigation.class);

        final ScriptExecutionDetails executionDetails = mock(ScriptExecutionDetails.class);
        when(detailsParser.getDetails(source)).thenReturn(executionDetails);

        final ScriptDocGenerator generator = mock(ScriptDocGenerator.class);
        whenNew(ScriptDocGenerator.class).withArguments(cclScript, cssDirectory, executionDetails, writer, navigation)
                .thenReturn(generator);

        testMojo.writeObjectDocumentation(cssDirectory, Collections.singletonList(doc), navigation);
        verify(generator).generate();
    }

    /**
     * Test the generation of documentation for a {@code .SUB} file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteObjectDocumentationSub() throws Exception {
        final IncludeFile includeFile = mock(IncludeFile.class);
        final File cssDirectory = mock(File.class);
        final Writer writer = mock(Writer.class);

        @SuppressWarnings("unchecked")
        final List<String> source = mock(List.class);
        final String sourceFilename = "i am the source filename";
        final File sourceFile = mock(File.class);
        when(sourceFile.getName()).thenReturn(sourceFilename);

        final Documentation doc = mock(Documentation.class);
        when(doc.getObjectType()).thenReturn(ObjectType.SUB);
        when(doc.getSourceFile()).thenReturn(sourceFile);

        final ParserInjectionMojo testMojo = new ParserInjectionMojo(textParser, detailsParser);
        testMojo.setIncludeFile(sourceFilename, includeFile);
        testMojo.setWriter(doc, writer);
        testMojo.setIncludeFile(sourceFilename, includeFile);
        testMojo.setSource(sourceFile, source);

        final Navigation navigation = mock(Navigation.class);

        final ScriptExecutionDetails executionDetails = mock(ScriptExecutionDetails.class);
        when(detailsParser.getDetails(source)).thenReturn(executionDetails);

        final IncludeDocGenerator generator = mock(IncludeDocGenerator.class);
        whenNew(IncludeDocGenerator.class)
                .withArguments(includeFile, cssDirectory, executionDetails, writer, navigation).thenReturn(generator);

        testMojo.writeObjectDocumentation(cssDirectory, Collections.singletonList(doc), navigation);
        verify(generator).generate();
    }

    /**
     * An extension of {@link CDocReportMojo} that separates the copying of CSS files from the actual copying to assist
     * in unit testing.
     *
     * @author Joshua Hyde
     *
     */
    protected static class CssCopyRecordingMojo extends CDocReportMojo {
        private final List<CopyInvocation> copyInvocations = new LinkedList<CopyInvocation>();

        /**
         * Create a mojo.
         *
         * @param textParser
         *            A {@link TextParser}.
         * @param detailsParser
         *            A {@link ScriptExecutionDetailsParser}.
         */
        public CssCopyRecordingMojo(final TextParser textParser, final ScriptExecutionDetailsParser detailsParser) {
            super(textParser, detailsParser);
        }

        /**
         * Get the CSS copy invocations.
         *
         * @return A {@link List} of {@link CopyInvocation} objects representing invocations of
         *         {@link CDocReportMojo#copyCss(File, String)}.
         */
        @SuppressWarnings("javadoc")
        public List<CopyInvocation> getCopyInvocations() {
            return copyInvocations;
        }

        @Override
        protected void copyCss(final File cssDirectory, final String cssFilename) {
            copyInvocations.add(new CopyInvocation(cssDirectory, cssFilename));
        }

        /**
         * A record of an invocation of {@link CDocReportMojo#copyCss(File, String)}.
         *
         * @author Joshua Hyde
         *
         */
        @SuppressWarnings("javadoc")
        public static class CopyInvocation {
            private final File cssDirectory;
            private final String cssFilename;

            /**
             * Create an invocation record.
             *
             * @param cssDirectory
             *            The directory to which the CSS file was to be copied.
             * @param cssFilename
             *            The filename of the CSS file that was to be copied.
             */
            public CopyInvocation(final File cssDirectory, final String cssFilename) {
                this.cssDirectory = cssDirectory;
                this.cssFilename = cssFilename;
            }

            /**
             * Get the CSS directory.
             *
             * @return The CSS directory.
             */
            public File getCssDirectory() {
                return cssDirectory;
            }

            /**
             * Get the CSS file's filename.
             *
             * @return The CSS file's filename.
             */
            public String getCssFilename() {
                return cssFilename;
            }
        }
    }

    /**
     * An extension of {@link CssCopyRecordingMojo} that allows for the injection of parsed objects, separating out the
     * logic of invoking the parsing from the actual parsing.
     *
     * @author Joshua Hyde
     *
     */
    protected static class ParserInjectionMojo extends CssCopyRecordingMojo {
        private final Map<String, CclScript> scripts = new HashMap<String, CclScript>();
        private final Map<String, IncludeFile> includes = new HashMap<String, IncludeFile>();
        private final Map<Documentation, Writer> writers = new HashMap<Documentation, Writer>();
        private final Map<File, List<String>> sources = new HashMap<File, List<String>>();

        /**
         * Create a mojo.
         *
         * @param textParser
         *            A {@link TextParser}.
         * @param detailsParser
         *            A {@link ScriptExecutionDetailsParser}.
         */
        public ParserInjectionMojo(final TextParser textParser, final ScriptExecutionDetailsParser detailsParser) {
            super(textParser, detailsParser);
        }

        /**
         * Set the product of parsing an include file.
         *
         * @param filename
         *            The name of the file to be the source for the production of the include file.
         * @param includeFile
         *            The {@link IncludeFile} to be returned as the product of parsing the given file.
         * @see #getIncludeFile(String, List)
         */
        @SuppressWarnings("javadoc")
        public void setIncludeFile(final String filename, final IncludeFile includeFile) {
            includes.put(filename, includeFile);
        }

        /**
         * Set the product of parsing a CCL script.
         *
         * @param filename
         *            The name of the file to be the source for the production of the script.
         * @param script
         *            The {@link CclScript} to be returned as the product of parsing the given file.
         * @see #getScript(String, List)
         */
        @SuppressWarnings("javadoc")
        public void setScript(final String filename, final CclScript script) {
            scripts.put(filename, script);
        }

        /**
         * Set the source to be read for a given file.
         *
         * @param file
         *            The {@link File} whose source is to be read.
         * @param source
         *            A {@link List} of {@link String} objects representing the source of the file.
         * @see #readSource(File)
         */
        @SuppressWarnings("javadoc")
        public void setSource(final File file, final List<String> source) {
            sources.put(file, source);
        }

        /**
         * Set the writer to be used for a given documentation object.
         *
         * @param doc
         *            The {@link Documentation} object for which a writer is to be produced.
         * @param writer
         *            The {@link Writer}
         */
        public void setWriter(final Documentation doc, final Writer writer) {
            writers.put(doc, writer);
        }

        @Override
        IncludeFile getIncludeFile(final String filename, final List<String> source) throws MavenReportException {
            return includes.get(filename);
        }

        @Override
        CclScript getScript(final String filename, final List<String> source) throws MavenReportException {
            return scripts.get(filename);
        }

        @Override
        Writer getWriter(final Documentation doc) throws MavenReportException {
            return writers.get(doc);
        }

        @Override
        List<String> readSource(final File file) throws MavenReportException {
            return sources.get(file);
        }
    }

    /**
     * An extension of {@link CDocReportMojo} that separates the {@link #executeReport(Locale)} method from its
     * delegating methods to facilitate unit testing.
     *
     * @author Joshua Hyde
     *
     */
    protected static class ExecutionRecordingMojo extends CssCopyRecordingMojo {
        private final List<WriteInvocation> summaryInvocations = new LinkedList<WriteInvocation>();
        private final List<WriteInvocation> objectDocInvocations = new LinkedList<WriteInvocation>();
        private final Map<List<File>, List<Documentation>> docs = new HashMap<List<File>, List<Documentation>>();
        private List<File> files = Collections.emptyList();
        private File destinationDirectory;

        /**
         * Create a mojo.
         *
         * @param textParser
         *            A {@link TextParser}.
         * @param detailsParser
         *            A {@link ScriptExecutionDetailsParser}.
         */
        public ExecutionRecordingMojo(final TextParser textParser, final ScriptExecutionDetailsParser detailsParser) {
            super(textParser, detailsParser);
        }

        /**
         * Add documentation that can be {@link #toDocumentation(List) returned}.
         *
         * @param files
         *            A {@link List} of {@link File} objects; this will be the list of files checked for when a
         *            conversion to documentation objects is requested.
         * @param doc
         *            A {@link List} of {@link Documentation}; when a conversion to documentation is
         *            {@link #toDocumentation(List) invoked}, the list of files given to the method must match the list
         *            of files given to <b>this</b> method in order for the given list of {@code Documentation} objects
         *            to be returned.
         * @see #toDocumentation(List)
         */
        @SuppressWarnings("javadoc")
        public void addDocumentation(final List<File> files, final List<Documentation> doc) {
            docs.put(files, doc);
        }

        /**
         * Get all invocations to {@link #writeDocumentationSummary(File, List)}.
         *
         * @return A {@link List} of {@link WriteInvocation} objects representing invocations to
         *         {@link #writeDocumentationSummary(File, List)}.
         */
        @SuppressWarnings("javadoc")
        public List<WriteInvocation> getSummaryInvocations() {
            return summaryInvocations;
        }

        /**
         * Get all invocations to {@link #writeObjectDocumentation(File, List, Navigation)}.
         *
         * @return A {@link List} of {@link WriteInvocation} objects representing invocations to
         *         {@link #writeObjectDocumentation(File, List, Navigation)}.
         */
        @SuppressWarnings("javadoc")
        public List<WriteInvocation> getObjectDocInvocations() {
            return objectDocInvocations;
        }

        /**
         * Set the destination directory to be {@link #getDestinationDirectory() returned}.
         *
         * @param destinationDirectory
         *            A {@link File}.
         * @see #getDestinationDirectory()
         */
        @SuppressWarnings("javadoc")
        public void setDestinationDirectory(final File destinationDirectory) {
            this.destinationDirectory = destinationDirectory;
        }

        /**
         * Set the files to be {@link #getFiles() returned}.
         *
         * @param files
         *            A {@link List} of {@link File} objects.
         * @see #getFiles()
         */
        @SuppressWarnings("javadoc")
        public void setFiles(final List<File> files) {
            this.files = files;
        }

        @Override
        File getDestinationDirectory() {
            return destinationDirectory;
        }

        @Override
        List<File> getFiles() throws MavenReportException {
            return files;
        }

        @Override
        List<Documentation> toDocumentation(final List<File> files) {
            if (!docs.containsKey(files)) {
                throw new IllegalArgumentException("No documentation found for files: " + files);
            }

            return docs.get(files);
        }

        @Override
        void writeDocumentationSummary(final File cssDirectory, final List<Documentation> documentation)
                throws MavenReportException {
            summaryInvocations.add(new WriteInvocation(cssDirectory, documentation, null));
        }

        @Override
        void writeObjectDocumentation(final File cssDirectory, final List<Documentation> documentation,
                final Navigation backNavigation) throws MavenReportException {
            objectDocInvocations.add(new WriteInvocation(cssDirectory, documentation, backNavigation));
        }

        /**
         * A description of an invocation to either {@link CDocReportMojo#writeDocumentationSummary(File, List)} or
         * {@link CDocReportMojo#writeObjectDocumentation(File, List, Navigation)}.
         *
         * @author Joshua Hyde
         *
         */
        @SuppressWarnings("javadoc")
        public static class WriteInvocation {
            private final File cssDirectory;
            private final List<Documentation> docs;
            private final Navigation backNavigation;

            /**
             * Create a write invocation.
             *
             * @param cssDirectory
             *            The CSS directory.
             * @param docs
             *            The documentation that was to be written.
             * @param backNavigation
             *            A {@link Navigation} object.
             */
            public WriteInvocation(final File cssDirectory, final List<Documentation> docs,
                    final Navigation backNavigation) {
                this.cssDirectory = cssDirectory;
                this.docs = docs;
                this.backNavigation = backNavigation;
            }

            /**
             * Get the back navigation.
             *
             * @return The back navigation.
             */
            public Navigation getBackNavigation() {
                return backNavigation;
            }

            /**
             * Get the CSS directory.
             *
             * @return The CSS directory.
             */
            public File getCssDirectory() {
                return cssDirectory;
            }

            /**
             * Get the documentation objects.
             *
             * @return The documentation objects.
             */
            public List<Documentation> getDocs() {
                return docs;
            }
        }
    }
}