package com.cerner.ftp;

import static org.fest.assertions.Assertions.assertThat;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.sftp.SftpDownloader;
import com.cerner.ftp.sftp.SftpUploader;
import com.google.code.jetm.reporting.BindingMeasurementRenderer;
import com.google.code.jetm.reporting.xml.XmlAggregateBinder;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests for SFTP upload and download.
 *
 * @author Joshua Hyde
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/applicationContext*.xml")
public class SftpITest {
    @Autowired
    @Qualifier("hostAddress")
    private String hostAddress;

    private static EtmMonitor monitor;

    @Autowired
    @Qualifier("hostUsername")
    private String hostUsername;

    @Autowired
    @Qualifier("hostPassword")
    private String hostPassword;

    /**
     * Configure and start the JETM monitor. Set credential properties based on server properties.
     *
     * @throws IOException
     *             not expected.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        BasicEtmConfigurator.configure();
        monitor = EtmManager.getEtmMonitor();
        monitor.start();

        Properties prop = new Properties();
        try (InputStream stream = SftpITest.class.getResourceAsStream("/spring/build.properties")) {
            prop.load(stream);
            String hostCredentialsId = prop.getProperty("ccl-hostCredentialsId");
            String keyFile = prop.getProperty("ccl-keyFile");
            if (hostCredentialsId != null && !hostCredentialsId.isEmpty()) {
                String username = prop.getProperty(String.format("settings.servers.%s.username", hostCredentialsId));
                String password = prop.getProperty(String.format("settings.servers.%s.password", hostCredentialsId));
                System.setProperty("ccl-hostUsername", username);
                System.setProperty("ccl-hostPassword", password);
            }
            if (keyFile != null && !keyFile.isEmpty()) {
                System.setProperty("ccl-keyFile", keyFile);
            }
        }
    }

    /**
     * Render out the timing results.
     *
     * @throws Exception
     *             If any errors occur during the teardown.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (monitor != null) {
            monitor.stop();

            final File timingDirectory = new File("target/jetm");
            FileUtils.forceMkdir(timingDirectory);

            final File outputFile = new File(timingDirectory, "SftpITest.xml");
            final FileWriter writer = new FileWriter(outputFile);
            try {
                monitor.render(new BindingMeasurementRenderer(new XmlAggregateBinder(), writer));
            } finally {
                writer.close();
            }
        }
    }

    /**
     * Test the uploading and downloading of a file. This assumes that "/tmp/" is a valid path on the remote server.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testUploadDownload() throws Exception {
        final File uploadFile = File.createTempFile("testUploadDownload", null);
        final File downloadFile = new File("target", "test.download.txt");

        FileUtils.deleteQuietly(uploadFile);
        FileUtils.deleteQuietly(downloadFile);

        final Random random = new Random();
        final String fileText = String.format("%d-%d", random.nextInt(), random.nextInt());
        FileUtils.writeLines(uploadFile, Collections.singleton(fileText));

        final URI remoteServerLocation = URI.create("/tmp/" + uploadFile.getName());

        final FileRequest uploadRequest = FileRequestFactory.create(uploadFile.toURI(), remoteServerLocation);
        final FileRequest downloadRequest = FileRequestFactory.create(remoteServerLocation, downloadFile.toURI());

        String keyFile = System.getProperty("ccl-keyFile");
        if (keyFile != null) {
            System.out.println("keyFile: " + keyFile);
            System.out.println("keyFileUri: " + (new File(keyFile)).toURI());
            System.out.println("keyFileUri: " + URI.create(keyFile));
        }
        final FtpProduct product = keyFile != null
                ? KeyCryptoBuilder.getBuilder().setUsername(hostUsername).setPrivateKey(URI.create(keyFile))
                        .setKeySalt(hostPassword).setServerAddress(URI.create(hostAddress)).build()
                : UserPassBuilder.getBuilder().setUsername(hostUsername).setPassword(hostPassword)
                        .setServerAddress(URI.create(hostAddress)).build();

        final Uploader uploader = SftpUploader.createUploader(product);
        uploader.upload(Collections.singleton(uploadRequest));

        final Downloader downloader = SftpDownloader.createDownloader(product);
        downloader.download(Collections.singleton(downloadRequest));

        assertThat(downloadFile).exists();
        assertThat(FileUtils.readLines(downloadFile, "UTF-8")).containsOnly(fileText);
    }
}
