package com.cerner.ccl.j4ccl.impl.commands.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.data.Environment;
import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;
import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.sftp.SftpDownloader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit test of {@link RecordDataExtractor}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { RecordDataExtractor.class, SftpDownloader.class, AuthHelper.class, File.class,
        PointFactory.class })
public class RecordDataExtractorTest {
    @Mock
    private Record record;
    @Mock
    private Environment environment;
    @Mock
    private Downloader downloader;
    @Mock
    private FtpProduct ftpProduct;

    /**
     * Set up mocks for each test.
     */
    @Before
    public void setUp() {
        when(record.getName()).thenReturn("request");
        when(environment.getCclUserDir()).thenReturn("ccluserdir");
        when(environment.getCerTemp()).thenReturn("cer_temp");
    }

    /**
     * Test the extraction of data from a flat file containing JSON.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExtractRecordData() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(RecordDataExtractor.class, "extractRecordData")).thenReturn(point);

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Field i2Field = mock(Field.class);
        when(i2Field.getName()).thenReturn("I2_FIELD");
        when(i2Field.getType()).thenReturn(DataType.I2);

        final Structure structure = mock(Structure.class);
        when(structure.getFields()).thenReturn(Arrays.asList(i2Field));

        final Record record = mock(Record.class);
        when(record.getName()).thenReturn("REPLY");
        when(record.getStructure()).thenReturn(structure);

        final RecordDataExtractor extractor = new RecordDataExtractor(record, environment);

        // Create a mock output file
        FileUtils.writeLines(extractor.getLocalDataLocation(), Arrays.asList("{\"REPLY\":{\"I2_FIELD\":123}}"));

        extractor.extractRecordData();
        verify(record, times(1)).setI2("I2_FIELD", (short) 123);
        verify(point).collect();
    }

    /**
     * Verify that the extract execution commands are generated properly.
     */
    @Test
    public void testGetExtractionCommands() {
        final RecordDataExtractor extractor = new RecordDataExtractor(record, environment);

        final List<String> expected = new ArrayList<String>(4);
        expected.add("call echojson(");
        expected.add(record.getName());
        expected.add(", '" + extractor.getRemoteDataLocation().getPath() + "'");
        expected.add(") go");

        assertThat(extractor.getExtractionCommands()).isEqualTo(expected);
    }

    /**
     * Verify that the output is specified as being placed in cer_temp.
     */
    @Test
    public void testGetRemoteDataLocation() {
        final RecordDataExtractor extractor = new RecordDataExtractor(record, environment);
        assertThat(extractor.getRemoteDataLocation().getPath()).startsWith(environment.getCerTemp());
    }

    /**
     * If the JSON is split across multiple lines, the record structure should still be constructed.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRecordDataTrailingSlash() throws Exception {
        final Downloader downloader = mock(Downloader.class);
        /*
         * Copy the JSON file to the location the extractor expects to find the file
         */
        doAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Collection<FileRequest> requests = (Collection<FileRequest>) invocation.getArguments()[0];
                final FileRequest request = requests.iterator().next();
                FileUtils.copyFile(new File("src/test/resources/json/trailingSlash.json"),
                        new File(request.getTargetFile()));
                return null;
            }
        }).when(downloader).download(ArgumentMatchers.<Collection<? extends FileRequest>> any());

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment).extractRecordData();
        assertThat(record.getVC("MULTILINE")).isEqualTo("FirstLine\"=\"S\"");
    }

    /**
     * If the ending slash is actually an escaped slash, then the ending slash should *not* be stripped off.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRecordDataEscapedEndingSlash() throws Exception {
        final Downloader downloader = mock(Downloader.class);
        /*
         * Copy the JSON file to the location the extractor expects to find the file
         */
        doAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Collection<FileRequest> requests = (Collection<FileRequest>) invocation.getArguments()[0];
                final FileRequest request = requests.iterator().next();
                FileUtils.copyFile(new File("src/test/resources/json/escapedEndingSlash.json"),
                        new File(request.getTargetFile()));
                return null;
            }
        }).when(downloader).download(ArgumentMatchers.<Collection<? extends FileRequest>> any());

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment).extractRecordData();
        assertThat(record.getVC("MULTILINE")).isEqualTo("FirstLine\\\"=\"S\"");
    }

    /**
     * If the ending slash is actually an escaped slash, then the ending slash should *not* be stripped off... even if
     * the line only contains slashes.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRecordDataOnlySlashesEven() throws Exception {
        final Downloader downloader = mock(Downloader.class);
        /*
         * Copy the JSON file to the location the extractor expects to find the file
         */
        doAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Collection<FileRequest> requests = (Collection<FileRequest>) invocation.getArguments()[0];
                final FileRequest request = requests.iterator().next();
                FileUtils.copyFile(new File("src/test/resources/json/onlySlashesEven.json"),
                        new File(request.getTargetFile()));
                return null;
            }
        }).when(downloader).download(ArgumentMatchers.<Collection<? extends FileRequest>> any());

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment).extractRecordData();
        assertThat(record.getVC("MULTILINE")).isEqualTo("FirstLine\\\\\\\"=\"S\"");
    }

    /**
     * If the ending slash follows an escaped slash (e.g., "\\\"), then the trailing slash should be removed while the
     * escaped slash (and its escaping slash) should be preserved.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRecordDataEscapedSlashTrailingSlash() throws Exception {
        final Downloader downloader = mock(Downloader.class);
        /*
         * Copy the JSON file to the location the extractor expects to find the file
         */
        doAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Collection<FileRequest> requests = (Collection<FileRequest>) invocation.getArguments()[0];
                final FileRequest request = requests.iterator().next();
                FileUtils.copyFile(new File("src/test/resources/json/escapedAndTrailingSlash.json"),
                        new File(request.getTargetFile()));
                return null;
            }
        }).when(downloader).download(ArgumentMatchers.<Collection<? extends FileRequest>> any());

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment).extractRecordData();
        assertThat(record.getVC("MULTILINE")).isEqualTo("FirstLine\\\"=\"S\"");
    }

    /**
     * If the ending slash follows an escaped slash (e.g., "\\\"), then the trailing slash should be removed while the
     * escaped slash (and its escaping slash) should be preserved... even if the line only contains slashes
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractRecordDataOnlySlashesOdd() throws Exception {
        final Downloader downloader = mock(Downloader.class);
        /*
         * Copy the JSON file to the location the extractor expects to find the file
         */
        doAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Collection<FileRequest> requests = (Collection<FileRequest>) invocation.getArguments()[0];
                final FileRequest request = requests.iterator().next();
                FileUtils.copyFile(new File("src/test/resources/json/onlySlashesOdd.json"),
                        new File(request.getTargetFile()));
                return null;
            }
        }).when(downloader).download(ArgumentMatchers.<Collection<? extends FileRequest>> any());

        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);

        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment).extractRecordData();
        assertThat(record.getVC("MULTILINE")).isEqualTo("FirstLine\\\\\"=\"S\"");
    }

    /**
     * Confirm that the temp data location will be cleaned up.
     *
     * @throws IOException
     *             Not expected but sometimes bad things happen.
     */
    @SuppressWarnings("unused")
    @Test
    public void testDataCleanup() throws IOException {
        mockStatic(File.class);
        final File tempDirectory = mock(File.class);
        final String username = System.getProperty("user.name").toLowerCase(Locale.getDefault()).replace("$", "");
        when(File.createTempFile(matches("j4ccl_dataout_" + username + "_-?\\d+\\.json"),
                ArgumentMatchers.<String> isNull())).thenReturn(tempDirectory);
        final Record record = RecordFactory.create("REPLY", StructureBuilder.getBuilder().addVC("MULTILINE").build());
        new RecordDataExtractor(record, environment);
        verify(tempDirectory).deleteOnExit();
    }
}