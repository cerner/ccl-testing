package com.cerner.ccl.j4ccl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cerner.ccl.j4ccl.adders.arguments.CharacterArgument;
import com.cerner.ccl.j4ccl.adders.arguments.FloatArgument;
import com.cerner.ccl.j4ccl.adders.arguments.IntegerArgument;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;
import com.google.code.jetm.reporting.BindingMeasurementRenderer;
import com.google.code.jetm.reporting.xml.XmlAggregateBinder;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for the j4ccl-ssh library.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/spring/junitIntegrationTests-applicationContext.xml")
public class CclExecutorITest {
    /**
     * A {@link Rule} used to identify the name of the current test.
     */
    @Rule
    public TestName testName = new TestName();
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Autowired
    private Subject subject;
    private static EtmMonitor monitor;

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
        try (InputStream stream = CclExecutorITest.class.getResourceAsStream("/spring/build.properties")) {
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
     * Write out the results of all of the test runs.
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

            final File timingFile = new File(timingDirectory, CclExecutorITest.class.getSimpleName() + ".xml");
            final FileWriter writer = new FileWriter(timingFile);
            try {
                monitor.render(new BindingMeasurementRenderer(new XmlAggregateBinder(), writer));
            } finally {
                writer.close();
            }
        }
    }

    @After
    public void teardown() throws Exception {
        FileUtils.copyFile(new File("target/ccl-log/ITest.log"),
                new File("target/ccl-log/ITest-" + testName.getMethodName() + ".log"));
    }

    /**
     * Verify that compilation and execution of a script succeeds.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCompileAndExecute() throws Exception {
        // First, compile the script
        final CclExecutor compileExecutor = CclExecutor.getExecutor();
        compileExecutor.addScriptCompiler(getLocalFile("j4ccl_testcompile.prg")).commit();
        Subject.doAs(subject, new ExecutorRunner(compileExecutor));

        // Now, run the script and verify that its echo was in the output
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        final CclExecutor execExecutor = CclExecutor.getExecutor();
        execExecutor.addScriptExecution("j4ccl_testCompile").commit();
        execExecutor.addScriptDropper("j4ccl_testCompile").commit();
        execExecutor.setOutputStream(outStream, OutputType.CCL_SESSION);
        Subject.doAs(subject, new ExecutorRunner(execExecutor));

        outStream.flush();
        assertThat(outStream.toString())
                .contains("I am a script used to test the compilation of scripts with j4ccl-ssh");
    }

    /**
     * Verify that an exception is thrown if the CCL results viewer is displayed by a CCL execution.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSelectWithoutNL() throws Exception {

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_selectwithoutnl.prg")).commit();

        expected.expect(CclCommandException.class);
        expected.expectMessage("Execution of script j4ccl_selectWithoutNL failed.");
        Subject.doAs(subject, new ExecutorRunner(executor));

        // First, compile the script
        final CclExecutor compileExecutor = CclExecutor.getExecutor();
        compileExecutor.addScriptCompiler(getLocalFile("j4ccl_selectwithoutnl.prg")).commit();
        Subject.doAs(subject, new ExecutorRunner(compileExecutor));

        try {
            final CclExecutor execExecutor = CclExecutor.getExecutor();
            execExecutor.addScriptExecution("j4ccl_selectWithoutNL").commit();
            Subject.doAs(subject, new ExecutorRunner(execExecutor));
        } catch (CclCommandException e) {
            assertThat(e.getCause().getClass().equals(SshException.class));
            assertThat(e.getCause().getMessage().equals("select without 'nl:' detected"));
            throw e;
        } finally {
            final CclExecutor execExecutor = CclExecutor.getExecutor();
            execExecutor.addScriptDropper("j4ccl_selectWithoutNL").commit();
            Subject.doAs(subject, new ExecutorRunner(execExecutor));
        }
    }

    /**
     * Test the default initialization of values.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDefaultInit() throws Exception {
        final Structure requestListStructure = StructureBuilder.getBuilder().addI4("dummy_field").build();
        final Structure requestStructure = StructureBuilder.getBuilder()
                .addDynamicList("list_field", requestListStructure).addI2("i2_field").addI4("i4_field")
                .addF8("f8_field").addVC("vc_field").addChar("char_field", 16).addDQ8("dq8_field").build();
        final Record request = RecordFactory.create("testRequest", requestStructure);

        final Structure replyStructure = StructureBuilder.getBuilder().addI2("list_matches").addI2("i2_matches")
                .addI2("i4_matches").addI2("f8_matches").addI2("vc_matches").addI2("char_matches").addI2("dq8_matches")
                .build();
        final Record reply = RecordFactory.create("testReply", replyStructure);

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_test_init.prg")).commit();
        executor.addScriptExecution("j4ccl_test_init").withReplace("request", request).withReplace("reply", reply)
                .commit();
        executor.addScriptDropper("j4ccl_test_init").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        for (final Field replyField : replyStructure.getFields())
            assertThat(reply.getI2Boolean(replyField.getName()))
                    .as("Field did not match (" + replyField.getName() + ")").isTrue();
    }

    /**
     * Verify that dropping a script works.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDropScript() throws Exception {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_testcompile.prg")).commit();
        executor.addScriptDropper("j4ccl_testCompile").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        // If the script was dropped, executing it should error
        final CclExecutor execExecutor = CclExecutor.getExecutor();
        execExecutor.addScriptExecution("j4ccl_testCompile").commit();
        execExecutor.setOutputStream(outStream, OutputType.CCL_SESSION);
        Subject.doAs(subject, new ExecutorRunner(execExecutor));

        // A CCL-E-43 error indicates that the script could not be executed
        outStream.flush();
        assertThat(outStream.toString()).contains("%CCL-E-43-CCL");
    }

    /**
     * Verify that dynamic includes work when a script name is specified.
     * 
     * @throws Exception
     *             If an error occurs while running the test.
     */
    @Test
    public void testDynamicIncludeSpecifiedName() throws Exception {
        final CclExecutor executor = CclExecutor.getExecutor();
        final String scriptName = "j4ccl_testIncludeWrapperScript";
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        executor.addDynamicCompiler(getLocalFile("j4ccl_testInclude.inc")).withScriptName(scriptName).commit();
        executor.addScriptExecution(scriptName).commit();
        executor.addScriptDropper(scriptName).commit();
        executor.setOutputStream(outStream, OutputType.CCL_SESSION);
        Subject.doAs(subject, new ExecutorRunner(executor));

        outStream.flush();
        assertThat(outStream.toString()).contains("I am a test include! Whoo-hoo!");
    }

    /**
     * Test that a script can be executed with a replacement of its record structures.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteWithReplace() throws Exception {
        final Structure recordStruct = StructureBuilder.getBuilder().addVC("vc_field").build();
        final Record requestRec = RecordFactory.create("request_test", recordStruct);
        final Record replyRec = RecordFactory.create("reply_test", recordStruct);

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_testrequestreply.prg")).commit();
        executor.addScriptExecution("j4ccl_testRequestReply").withReplace("request", requestRec)
                .withReplace("reply", replyRec).commit();
        executor.addScriptDropper("j4ccl_testRequestReply").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        assertThat(requestRec.getVC("vc_field")).isEqualTo(replyRec.getVC("vc_field"));
    }

    /**
     * Verify that a timeout does not occur when executing a CCL command that generates output similar to the 
     * CCL prompt but not "  1)".
     * 
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testExecuteWithPromptLikeOutput() throws Exception {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final Structure requestStruct = StructureBuilder.getBuilder().addVC("vc_field_one").addVC("vc_field_two")
                .addVC("vc_field_three").addVC("vc_field_four").addVC("vc_field_five").addVC("vc_field_six")
                .addVC("vc_field_seven").addVC("vc_field_eigth").addVC("vc_field_nine").addVC("vc_field_ten")
                .addVC("vc_field_eleven").addVC("vc_field_twelve").addVC("vc_field_thirteen").addVC("vc_field_fourteen")
                .addVC("vc_field_fifteen").addVC("vc_field_sixteen").addVC("vc_field_seventeen")
                .addVC("vc_field_eighteen").addVC("vc_field_nineteen").addVC("vc_field_twenty").build();
        final Record requestRec = RecordFactory.create("request_test", requestStruct);
        final Structure replyStruct = StructureBuilder.getBuilder().addStatusData().build();
        final Record replyRec = RecordFactory.create("reply_test", replyStruct);

        final CclExecutor executor = CclExecutor.getExecutor();

        executor.addScriptCompiler(getLocalFile("j4ccl_ccllikeoutput.prg")).commit();
        executor.addScriptExecution("j4ccl_cclLikeOutput").withReplace("request", requestRec)
                .withReplace("reply", replyRec).commit();
        executor.addScriptDropper("j4ccl_cclLikeOutput").commit();
        executor.setOutputStream(outStream, OutputType.FULL_DEBUG);
        Subject.doAs(subject, new ExecutorRunner(executor));
        assertThat(replyRec.getRecord("status_data").getChar("status")).isEqualTo("S");

        outStream.flush();
        String outStreamData = outStream.toString();
        assertThat(outStreamData).contains("everything made it");

        FileUtils.writeStringToFile(new File("target/ccl-log/ITest.log"), outStreamData, Charset.forName("utf-8"),
                false);
    }

    /**
     * Test the execution of a script with arguments. Implicitly, this also tests the case of using a WITH REPLACE since
     * a reply record structure is used to retrieve the values passed in as arguments.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteWithArguments() throws Exception {
        final Structure valuesStructure = StructureBuilder.getBuilder().addF8("f8Arg").addVC("singleQuoteArg")
                .addVC("doubleQuoteArg").addI4("integerArg").build();
        final Structure typesStructure = StructureBuilder.getBuilder().addVC("argType").build();
        final Structure replyStructure = StructureBuilder.getBuilder().addRecord("values", valuesStructure)
                .addList("types", typesStructure, 4).build();
        final Record testReply = RecordFactory.create("testReply", replyStructure);

        final double f8ArgValue = 1.25;
        final String singleQuoteValue = "has'";
        final String doubleQuoteValue = "has\"";
        final int integerValue = 32;

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_testargumentcall.prg")).commit();
        executor.addScriptExecution("j4ccl_testArgumentCall")
                .withArguments(new FloatArgument(1, 25), new CharacterArgument(singleQuoteValue),
                        new CharacterArgument(doubleQuoteValue), new IntegerArgument(integerValue))
                .withReplace("reply", testReply).commit();
        executor.addScriptDropper("j4ccl_testArgumentCall").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        final Record values = testReply.getRecord("values");
        assertThat(values.getF8("f8Arg")).isEqualTo(f8ArgValue);
        assertThat(values.getVC("singleQuoteArg")).isEqualTo(singleQuoteValue);
        assertThat(values.getVC("doubleQuoteArg")).isEqualTo(doubleQuoteValue);
        assertThat(values.getI4("integerArg")).isEqualTo(integerValue);

        // Verify that CCL interpreted the data types correctly
        final RecordList types = testReply.getList("types");
        assertThat(types.get(0).getVC("argType")).isEqualTo("F8");
        assertThat(types.get(1).getVC("argType")).isEqualTo("C" + singleQuoteValue.length());
        assertThat(types.get(2).getVC("argType")).isEqualTo("C" + doubleQuoteValue.length());
        assertThat(types.get(3).getVC("argType")).isEqualTo("I4");
    }

    /**
     * Test that the full debug mode outputs things outside of the CCL session.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFullDebugOutput() throws Exception {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_testrequestreply.prg")).commit();
        executor.addScriptDropper("j4ccl_testRequestReply").commit();
        executor.setOutputStream(outStream, OutputType.FULL_DEBUG);
        Subject.doAs(subject, new ExecutorRunner(executor));

        // The output should contain at least the envset
        assertThat(outStream.toString("utf-8"))
                .contains("envset " + getPrincipal(MillenniumDomainPrincipal.class).getDomainName());
    }

    /**
     * If a VC variable's data exceeds 132 characters, it should be broken up into a series of concat() calls to handle
     * the excessive length.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLongVarchar() throws Exception {
        final String stringValue = "This is an excessively long string.  It's intended to test a situation both where it exceeds the 132-character maximum width of CCL *and* has a nested single quote (see \"it's\" <- look, it even has double quotes!).";

        final Record request = RecordFactory.create("request_test",
                StructureBuilder.getBuilder().addVC("source").build());
        request.setVC("source", stringValue);

        final Record reply = RecordFactory.create("reply_test", StructureBuilder.getBuilder().addVC("target").build());

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_copytoreply.prg")).commit();
        executor.addScriptExecution("j4ccl_copyToReply").withReplace("request", request).withReplace("reply", reply)
                .commit();
        executor.addScriptDropper("j4ccl_copyToReply").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        assertThat(reply.getVC("target")).isEqualTo(stringValue);
    }

    /**
     * If the reading of the file downloaded from CCL is anything other than UTF-8, then characters with code points
     * that don't overlap with the platform encoding (i.e., Windows-1252) will fail to be read properly and the string
     * read from CCL will not match the string sent in. <br>
     * This test assumes that the platform encoding is non-UTF-8. It will not fail on a UTF-8 platform, but, if you
     * "unfix" the issue verified to be resolved by this test, you will not be able to reproduce the issue.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testReadUtf8() throws Exception {
        final String stringValue = "/*Don�t join DCD to DAC */";
        final Record request = RecordFactory.create("request_test",
                StructureBuilder.getBuilder().addVC("source").build());
        request.setVC("source", stringValue);

        final Record reply = RecordFactory.create("reply_test", StructureBuilder.getBuilder().addVC("target").build());

        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(getLocalFile("j4ccl_copytoreply.prg")).commit();
        executor.addScriptExecution("j4ccl_copyToReply").withReplace("request", request).withReplace("reply", reply)
                .commit();
        executor.addScriptDropper("j4ccl_copyToReply").commit();
        Subject.doAs(subject, new ExecutorRunner(executor));

        assertThat(reply.getVC("target")).isEqualTo(stringValue);
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
     * Get a principal off of the current subject.
     * 
     * @param principalClazz
     *            The {@link Class} of the principal to be retrieved.
     * @return The desired principal.
     */
    @SuppressWarnings("unchecked")
    private <P extends Principal> P getPrincipal(final Class<P> principalClazz) {
        return (P) Subject.doAs(subject, new PrivilegedAction<P>() {
            public P run() {
                return JaasUtils.getPrincipal(principalClazz);
            }
        });
    }

    /**
     * Get a private credential off of the current subject.
     * 
     * @param credentialClazz
     *            The {@link Class} of the credential to be retrieved.
     * @return The desired credential.
     */
    @SuppressWarnings("unchecked")
    private <T> T getPrivateCredential(final Class<T> credentialClazz) {
        return (T) Subject.doAs(subject, new PrivilegedAction<T>() {
            public T run() {
                return JaasUtils.getPrivateCredential(credentialClazz);
            }
        });
    }

    /**
     * Create a set.
     * 
     * @param args
     *            A varargs to be converted into a set.
     * @return A {@link Set} created out of the given objects.
     */
    @SuppressWarnings("unchecked")
    private <T> Set<T> asSet(Object... args) {
        final Set<Object> set = new HashSet<Object>();
        for (Object arg : args)
            set.add(arg);
        return (Set<T>) set;
    }

    /**
     * A {@link PrivilegedExceptionAction} that runs a given CCL executor.
     * 
     * @author Joshua Hyde
     * 
     */
    private static class ExecutorRunner implements PrivilegedExceptionAction<Void> {
        private final CclExecutor executor;

        /**
         * Create an executor action.
         * 
         * @param executor
         *            The {@link CclExecutor} to be ran.
         */
        public ExecutorRunner(CclExecutor executor) {
            this.executor = executor;
        }

        /**
         * {@inheritDoc}
         */
        public Void run() throws Exception {
            executor.execute();
            return null;
        }
    }
}
