package com.cerner.ccl.j4ccl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.Configuration;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cerner.ccl.j4ccl.impl.CclResourceUploaderImpl;
import com.cerner.ccl.j4ccl.impl.data.Environment;
import com.cerner.ccl.j4ccl.ssh.CommandExpectationGroup;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.TerminalResponse;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;
import com.google.code.jetm.reporting.BindingMeasurementRenderer;
import com.google.code.jetm.reporting.xml.XmlAggregateBinder;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for {@link CclResourceUploaderImpl}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/junitIntegrationTests-applicationContext.xml")
public class CclResourceUploaderITest {
    private static EtmMonitor monitor;

    @Autowired
    private Subject subject;
    private CclResourceUploader uploader;
    private final Set<String> queuedDeletes = new HashSet<String>();

    /**
     * A {@link Rule} used to identify the name of the current test.
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * Configure and start the JETM monitor. Set credential properties based on server properties.
     * 
     * @throws IOException
     *             Not expected but sometimes bad things happen.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        BasicEtmConfigurator.configure();

        monitor = EtmManager.getEtmMonitor();
        monitor.start();

        Properties prop = new Properties();
        try (InputStream stream = CclResourceUploaderITest.class.getResourceAsStream("/spring/build.properties")) {
            prop.load(stream);
            String hostCredentialsId = prop.getProperty("ccl-hostCredentialsId");
            String cclCredentialsId = prop.getProperty("ccl-frontendCredentialsId");
            if (hostCredentialsId != null && !hostCredentialsId.isEmpty()) {
                String hostUsername = prop
                        .getProperty(String.format("settings.servers.%s.username", hostCredentialsId));
                String hostPassword = prop
                        .getProperty(String.format("settings.servers.%s.password", hostCredentialsId));
                System.setProperty("ccl-hostUsername", hostUsername);
                System.setProperty("ccl-hostPassword", hostPassword);
            }
            if (cclCredentialsId != null && !cclCredentialsId.isEmpty()) {
                String cclUsername = prop.getProperty(String.format("settings.servers.%s.username", cclCredentialsId));
                String cclPassword = prop.getProperty(String.format("settings.servers.%s.password", cclCredentialsId));
                System.setProperty("ccl-domainUsername", cclUsername);
                System.setProperty("ccl-domainPassword", cclPassword);
            }
            String cclHost = prop.getProperty("ccl-host");
            String cclDomain = prop.getProperty("ccl-domain");
            String hostUsername = System.getProperty("ccl-hostUsername");
            String osPromptPattern = prop.getProperty("ccl-osPromptPattern");
            if (osPromptPattern == null || osPromptPattern.isEmpty()) {
                osPromptPattern = TerminalProperties.constructDefaultOsPromptPattern(cclHost, cclDomain, hostUsername);
            }
            TerminalProperties.setGlobalTerminalProperties(TerminalProperties.getNewBuilder()
                    .setOsPromptPattern(osPromptPattern)
                    .setSpecifyDebugCcl(true).setLogfileLocation("target/ccl-log/ITest.log").build());
        }
    }

    /**
     * Create an uploader for each test and clear the deletion queue.
     */
    @Before
    public void setUp() {
        uploader = CclResourceUploader.getUploader();
        queuedDeletes.clear();
    }

    /**
     * Delete every file queued for deletion.
     * 
     * @throws Exception
     *             If any errors occur while performing tear-down steps.
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.copyFile(new File("target/ccl-log/ITest.log"),
                new File("target/ccl-log/ITest-" + testName.getMethodName() + ".log"));
        // Don't leave testing artifacts behind.
        if (!queuedDeletes.isEmpty()) {
            final List<String> commands = new ArrayList<String>(queuedDeletes.size());
            for (final String path : queuedDeletes)
                commands.add("rm -f " + path);

            Subject.doAs(subject, new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    CclResourceUploaderITest.executeCommandsHelper(commands);
                    return null;
                }
            });
        }
    }

    /**
     * Write out the results of the timing reports.
     * 
     * @throws Exception
     *             If any errors occur during the write-out.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (monitor != null) {
            monitor.stop();

            final File timingDirectory = new File("target/jetm");
            FileUtils.forceMkdir(timingDirectory);

            final File timingFile = new File(timingDirectory, CclResourceUploaderITest.class.getSimpleName() + ".xml");
            final FileWriter writer = new FileWriter(timingFile);
            try {
                monitor.render(new BindingMeasurementRenderer(new XmlAggregateBinder(), writer));
            } finally {
                writer.close();
            }
        }
    }

    /**
     * Verify that the implementation of {@link CclResourceUploader} can be found.
     */
    @Test
    public void testLookup() {
        assertThat(uploader).isInstanceOf(CclResourceUploaderImpl.class);
    }

    /**
     * This test confirms that the verifyFileExistance function actually returns false if the file does not exist.
     * 
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testMissingFile() throws Exception {
        verifyFileExistence(getEnvironment().getCclSource() + "/SurelyThisIsNotAValidFile.nonfile", false);
    }

    /**
     * Test the uploading of a COM script.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testUploadComFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testCom.com"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCerProc() + "/j4ccl_testCom.com", true);
    }

    /**
     * Verify that an INC file is uploaded to the appropriate location.
     * 
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testUploadIncFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testinclude.inc"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCclSource() + "/j4ccl_testinclude.inc", true);
    }

    /**
     * Test the uploading a Korn shell file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testUploadKshFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testShell.ksh"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCerProc() + "/j4ccl_testShell.ksh", true);
    }

    /**
     * Verify the .PRG files are uploaded to the correct directory.
     * 
     * @throws Exception
     *             If any errors occur during the upload.
     */
    @Test
    public void testUploadPrgFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testCompile.prg"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCclSource() + "/j4ccl_testcompile.prg", true);
    }

    /**
     * Verify that .SUB files are uploaded to the correct directory.
     * 
     * @throws Exception
     *             If any errors occur during the upload.
     */
    @Test
    public void testUploadSubFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testsub.sub"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCclSource() + "/j4ccl_testsub.sub", true);
    }

    /**
     * Test the uploading of a text file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testUploadTextFile() throws Exception {
        uploader.queueUpload(getLocalFile("j4ccl_testTextFile.txt"));
        Subject.doAs(subject, new PrivilegedAction<Void>() {
            public Void run() {
                uploader.upload();
                return null;
            }
        });

        verifyFileExistence(getEnvironment().getCerInstall() + "/j4ccl_testTextFile.txt", true);
    }

    /**
     * Get the current environment within the context of the available subject.
     * 
     * @return An {@link Environment}.
     */
    private Environment getEnvironment() {
        return (Environment) Subject.doAs(subject, new PrivilegedAction<Environment>() {
            public Environment run() {
                return Environment.getEnvironment();
            }
        });
    }

    /**
     * Get a file located in the same package as this class on the hard disk.
     * 
     * @param fileName
     *            The name of the file to be retrieved.
     * @return A {@link File} reference to the desired file.
     * @throws URISyntaxException
     *             If the URL pointing to the desired file is not a well-formed URI.
     */
    private File getLocalFile(final String fileName) throws URISyntaxException {
        return new File(getClass().getResource(fileName).toURI());
    }

    /**
     * Queue a file to be deleted from the remote server.
     * 
     * @param path
     *            The path of the file to be deleted.
     */
    private void queueDeletion(final String path) {
        queuedDeletes.add(path);
    }

    /**
     * Queue a file for download and verify that the file exists on the remote server using the class-level
     * {@link Configuration} object.
     * 
     * @param filePath
     *            The file's remote location that is to be verified.
     * @throws SshException
     *             If any errors occur while verifying the file's existence.
     * @throws PrivilegedActionException
     */
    private void verifyFileExistence(final String filePath, boolean exists) throws PrivilegedActionException {
        queueDeletion(filePath);
        boolean fileExists = (Boolean) Subject.doAs(subject, new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws Exception {
                return Boolean.valueOf(FileVerifier.verify(filePath));
            }
        });
        assertThat(exists ? fileExists : !fileExists);
    }

    /**
     * An object used to verify that a file exists on a remote server.
     * 
     * @author Joshua Hyde
     * 
     */
    private static class FileVerifier {
        /**
         * Verify that a file exists on a remote server.
         * 
         * @param remoteLocation
         *            The expected location of the file.
         * @return {@code true} if the file exists; {@code false} if otherwise.
         * @throws SshException
         *             If any errors occur while establishing a connection to the remote server.
         */
        public static boolean verify(final String remoteLocation) throws SshException {
            String lsOutput = executeCommandsHelper(Collections.singletonList("ls -l " + remoteLocation)).getOutput();
            return !lsOutput.contains("0653-341") && !lsOutput.contains("No such file or directory");
        }
    }

    private static TerminalResponse executeCommandsHelper (final List<String> commands) throws SshException{
        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(commands);
        commandExpectationGroup
                .addExpectation(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());
        commandExpectationGroups.add(commandExpectationGroup);

        return new JSchSshTerminal().executeCommandGroups(commandExpectationGroups);
    }
}