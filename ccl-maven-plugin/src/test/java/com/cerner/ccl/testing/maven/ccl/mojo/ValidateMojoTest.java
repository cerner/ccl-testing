package com.cerner.ccl.testing.maven.ccl.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.InputStream;

import javax.security.auth.Subject;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.adders.ScriptExecutionAdder;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * Unit tests for {@link ValidateMojo}.
 *
 * @author Joshua Hyde
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DefaultArtifactVersion.class, SAXBuilder.class, ValidateMojo.class, VersionRange.class })
public class ValidateMojoTest {
    @Mock
    private ValidationRule validationRule;
    @Mock
    private CclExecutor executor;
    private SubstitutedMojo mojo;

    /**
     * Set up the mojo for each test.
     */
    @Before
    public void setUp() {
        mojo = new SubstitutedMojo();
        mojo.setCclExecutor(executor);
        mojo.validationRule = validationRule;
    }

    /**
     * If the range returned by the testing framework falls within the range, then no errors should occur.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testValidateFrameworkInRange() throws Exception {
        testFrameworkVersionValidation(true, "1.14838384748", "[1,2]");
    }

    /**
     * If the range returned by the testing framework is outside of the configured range, then the validation should
     * error.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testValidateFrameworkOutOfRange() throws Exception {
        final String actualVersion = "1.14838384748";
        final String expectedVersionRange = "[3,23)";
        MojoFailureException e = assertThrows(MojoFailureException.class, () -> {
            testFrameworkVersionValidation(false, actualVersion, expectedVersionRange);
        });
        assertThat(e.getMessage()).isEqualTo("Unexpected CCL testing framework version: version " + actualVersion
                + " is not in the range " + expectedVersionRange);
    }

    /**
     * If the {@link BaseCclMojo#skipProcessing} parameter is set to {@code true}, then the mojo should not do any
     * validation.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipProcessing() throws Exception {
        mojo.skipProcessing = true;
        final Log log = mock(Log.class);
        mojo.setLog(log);

        mojo.execute();
        verifyNoInteractions(executor);
        verify(log).info("Validation is skipped.");
    }

    /**
     * Test the validation of range data for the CCL testing framework.
     *
     * @param pass
     *            A boolean; if {@code true}, then the validation will indicate that the actual version should be
     *            considered within the bounds of the expected version.
     * @param actualVersion
     *            The version to be passed back from the CCL testing framework.
     * @param expectedVersionRange
     *            The range to be used for comparison and determination of the boundaries of acceptable values.
     * @throws Exception
     *             If any errors occur during this validation.
     */
    private void testFrameworkVersionValidation(final boolean pass, final String actualVersion,
            final String expectedVersionRange) throws Exception {
        when(validationRule.getTestFrameworkVersion()).thenReturn(expectedVersionRange);
        final String stateResponse = "<?xml version=\"1.0\"?><ROOT><VERSION>" + actualVersion + "</VERSION></ROOT>";

        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class);
        final StateInjector injectorAnswer = new StateInjector(adder, stateResponse);
        when(executor.addScriptExecution("cclut_get_framework_state")).thenReturn(adder);
        when(adder.withReplace(eq("reply"), any(Record.class))).thenAnswer(injectorAnswer);

        final VersionRange versionRange = mock(VersionRange.class);
        mockStatic(VersionRange.class);
        when(VersionRange.createFromVersionSpec(expectedVersionRange)).thenReturn(versionRange);

        final Document document = mock(Document.class);
        final Element rootElement = mock(Element.class);
        when(document.getRootElement()).thenReturn(rootElement);
        when(rootElement.getChildText("VERSION")).thenReturn(actualVersion);

        final SAXBuilder saxBuilder = mock(SAXBuilder.class);
        whenNew(SAXBuilder.class).withNoArguments().thenReturn(saxBuilder);
        when(saxBuilder.build(any(InputStream.class))).thenReturn(document);

        final DefaultArtifactVersion version = mock(DefaultArtifactVersion.class);
        whenNew(DefaultArtifactVersion.class).withArguments(actualVersion).thenReturn(version);
        when(versionRange.containsVersion(version)).thenReturn(Boolean.valueOf(pass));

        mojo.setSubject(new Subject());
        mojo.execute();

        // Verify that the script execution was committed and executed
        verify(adder).commit();
        verify(executor).execute();
    }

    /**
     * An implementation of {@link ValidateMojo} that facilitates the injection of {@link CclExecutor} objects.
     *
     * @author Joshua Hyde
     *
     */
    private static class SubstitutedMojo extends ValidateMojo {
        private CclExecutor executor;
        private Subject subject;

        public SubstitutedMojo() {

        }

        /**
         * Set the CCL executor to be used by this mojo.
         *
         * @param executor
         *            The {@link CclExecutor} to be used by this mojo.
         */
        public void setCclExecutor(final CclExecutor executor) {
            this.executor = executor;
        }

        /**
         * Set the {@link Subject} to be used by this mojo.
         *
         * @param subject
         *            The {@link Subject} to be used by this mojo.
         */
        public void setSubject(final Subject subject) {
            this.subject = subject;
        }

        @Override
        protected CclExecutor createCclExecutor() throws MojoExecutionException, MojoFailureException {
            return executor == null ? super.createCclExecutor() : executor;
        }

        @Override
        protected Subject getSubject() throws MojoExecutionException {
            return subject == null ? super.getSubject() : subject;
        }
    }

    /**
     * An {@link Answer} used to inject the response of the CCL testing framework's state into the given record. This is
     * intended to be used with a mocking of {@link ScriptExecutionAdder#withReplace(String, Record)}.
     *
     * @author Joshua Hyde
     *
     */
    private static class StateInjector implements Answer<ScriptExecutionAdder> {
        private final ScriptExecutionAdder adder;
        private final String state;

        /**
         * Create an injector.
         *
         * @param adder
         *            The {@link ScriptExecutionAdder} to be returned by this answer when it is
         *            {@link #answer(InvocationOnMock) executed}.
         * @param state
         *            The state response to be injected into the reply record structure.
         */
        public StateInjector(final ScriptExecutionAdder adder, final String state) {
            this.adder = adder;
            this.state = state;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ScriptExecutionAdder answer(final InvocationOnMock invocation) throws Throwable {
            if (invocation.getArguments()[1] instanceof Record) {
                ((Record) invocation.getArguments()[1]).setVC("STATE", state);
            }
            return adder;
        }

    }
}
