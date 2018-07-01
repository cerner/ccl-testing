package com.cerner.ccl.testing.maven.ccl.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Collection;

import javax.security.auth.Subject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccl.testing.maven.ccl.mojo.BaseCclMojo;
import com.cerner.ccl.testing.maven.ccl.util.CclLogFileOutputStream;
import com.cerner.ccl.testing.maven.ccl.util.DelegatingOutputStream;
import com.cerner.ccl.testing.maven.ccl.util.LogOutputStreamProxy;

/**
 * Unit tests for {@link BaseCclMojo}.
 *
 * @author Joshua Hyde
 *
 */
@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { BackendNodePasswordCredential.class, BackendNodePrincipal.class, BaseCclMojo.class,
        CclExecutor.class, MillenniumDomainPrincipal.class, MillenniumDomainPasswordCredential.class })
public class BaseCclMojoTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private CclExecutor executor;
    private StubCclMojo mojo;

    /**
     * Create a new mojo for each test.
     *
     * @throws Exception
     *             If any errors occur.
     */
    @Before
    public void setUp() throws Exception {
        mojo = new StubCclMojo();
    }

    /**
     * Test the addition of backend information to the subject.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformation() throws Exception {
        final String environmentName = "environment.name";
        final String host = "host";
        final String hostUsername = "host.username";
        final String hostPassword = "host.password";

        mojo.hostUsername = hostUsername;
        mojo.hostPassword = hostPassword;
        mojo.environment = environmentName;
        mojo.host = host;

        final BackendNodePrincipal principal = mock(BackendNodePrincipal.class);
        whenNew(BackendNodePrincipal.class).withArguments(hostUsername, host, environmentName).thenReturn(principal);

        final BackendNodePasswordCredential credential = mock(BackendNodePasswordCredential.class);
        whenNew(BackendNodePasswordCredential.class).withArguments(hostPassword).thenReturn(credential);

        final Subject subject = new Subject();
        mojo.addBackendInformation(subject);
        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);
    }

    /**
     * Construction of a principal with no server ID and a blank username should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformationBlankUsername() throws Exception {
        expected.expect(MojoExecutionException.class);
        expected.expectMessage("A valid host username must be provided.");
        mojo.addBackendInformation(new Subject());
    }

    /**
     * Addition of information to a {@code null} {@link Subject} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformationNullSubject() throws Exception {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Subject cannot be null.");
        mojo.addBackendInformation(null);
    }

    /**
     * If there is a server ID configured, then that server should be used.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformationWithId() throws Exception {
        final String serverId = "a.server.id";
        final String environmentName = "environment.name";
        final String host = "host";
        final String hostUsername = "host.username";
        final String hostPassword = "host.password";
        final String encryptedPassword = StringUtils.reverse(hostPassword);

        final SecDispatcher dispatcher = mock(SecDispatcher.class);
        when(dispatcher.decrypt(encryptedPassword)).thenReturn(hostPassword);

        final Server server = mock(Server.class);
        when(server.getUsername()).thenReturn(hostUsername);
        when(server.getPassword()).thenReturn(encryptedPassword);

        final Settings settings = mock(Settings.class);
        when(settings.getServer(serverId)).thenReturn(server);

        mojo.environment = environmentName;
        mojo.host = host;
        mojo.hostCredentialsId = serverId;
        mojo.settings = settings;
        mojo.securityDispatcher = dispatcher;

        final BackendNodePrincipal principal = mock(BackendNodePrincipal.class);
        whenNew(BackendNodePrincipal.class).withArguments(hostUsername, host, environmentName).thenReturn(principal);

        final BackendNodePasswordCredential credential = mock(BackendNodePasswordCredential.class);
        whenNew(BackendNodePasswordCredential.class).withArguments(hostPassword).thenReturn(credential);

        final Subject subject = new Subject();
        mojo.addBackendInformation(subject);
        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);
    }

    /**
     * Construction with a non-existent server ID should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformationWithIdNoSuchServer() throws Exception {
        final String serverId = "a.server.id";
        mojo.hostCredentialsId = serverId;
        mojo.settings = mock(Settings.class);

        expected.expect(MojoExecutionException.class);
        expected.expectMessage("No backend <server /> found by the given ID: " + serverId);
        mojo.addBackendInformation(new Subject());
    }

    /**
     * Even if the host username and password are set, a server ID should be given priority over it.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddBackendInformationWithIdPrioritized() throws Exception {
        final String serverId = "a.server.id";
        final String environmentName = "environment.name";
        final String host = "host";
        final String hostUsername = "host.username";
        final String hostPassword = "host.password";
        final String encryptedPassword = StringUtils.reverse(hostPassword);

        final SecDispatcher dispatcher = mock(SecDispatcher.class);
        when(dispatcher.decrypt(encryptedPassword)).thenReturn(hostPassword);

        final Server server = mock(Server.class);
        when(server.getUsername()).thenReturn(hostUsername);
        when(server.getPassword()).thenReturn(encryptedPassword);

        final Settings settings = mock(Settings.class);
        when(settings.getServer(serverId)).thenReturn(server);

        final Log log = mock(Log.class);

        mojo.setLog(log);
        mojo.environment = environmentName;
        mojo.host = host;
        mojo.hostCredentialsId = serverId;
        mojo.settings = settings;
        mojo.securityDispatcher = dispatcher;
        mojo.hostUsername = hostUsername + "-unused";

        final BackendNodePrincipal principal = mock(BackendNodePrincipal.class);
        whenNew(BackendNodePrincipal.class).withArguments(hostUsername, host, environmentName).thenReturn(principal);

        final BackendNodePasswordCredential credential = mock(BackendNodePasswordCredential.class);
        whenNew(BackendNodePasswordCredential.class).withArguments(hostPassword).thenReturn(credential);

        final Subject subject = new Subject();
        mojo.addBackendInformation(subject);
        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);

        verify(log).warn(
                "A host username or password was provided as well as a credentials ID; the ID will be used over the username and password.");
    }

    /**
     * Test the addition of domain login information.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformation() throws Exception {
        final String domainName = "a.domain.name";
        final String username = "a.domain.username";
        final String password = "a.domain.password";

        final MillenniumDomainPrincipal principal = mock(MillenniumDomainPrincipal.class);
        whenNew(MillenniumDomainPrincipal.class).withArguments(username, domainName).thenReturn(principal);

        final MillenniumDomainPasswordCredential credential = mock(MillenniumDomainPasswordCredential.class);
        whenNew(MillenniumDomainPasswordCredential.class).withArguments(password).thenReturn(credential);

        final Subject subject = new Subject();

        mojo.domain = domainName;
        mojo.domainUsername = username;
        mojo.domainPassword = password;

        mojo.addDomainLoginInformation(subject);

        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);
    }

    /**
     * Adding domain login information with a blank username should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationBlankUsername() throws Exception {
        expected.expect(MojoExecutionException.class);
        expected.expectMessage("A valid frontend username must be provided when domain is specified.");

        mojo.domain = "a.domain";
        mojo.addDomainLoginInformation(new Subject());
    }

    /**
     * If there is no domain name set, then no domain credentials should be added to the subject.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationNoDomainName() throws Exception {
        final Subject subject = new Subject();
        mojo.addDomainLoginInformation(subject);
        assertThat(subject.getPrincipals()).isEmpty();
        assertThat(subject.getPrivateCredentials()).isEmpty();
    }

    /**
     * Addition of domain login information to a {@code null} {@link Subject} should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationNullSubject() throws Exception {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Subject cannot be null.");
        mojo.addDomainLoginInformation(null);
    }

    /**
     * Test the addition of domain login information from a configured server ID.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationWithServerId() throws Exception {
        final String serverId = "a.server.id";
        final String domainName = "a.domain.name";
        final String username = "a.domain.username";
        final String password = "a.domain.password";
        final String encryptedPassword = StringUtils.reverse(password);

        final MillenniumDomainPrincipal principal = mock(MillenniumDomainPrincipal.class);
        whenNew(MillenniumDomainPrincipal.class).withArguments(username, domainName).thenReturn(principal);

        final MillenniumDomainPasswordCredential credential = mock(MillenniumDomainPasswordCredential.class);
        whenNew(MillenniumDomainPasswordCredential.class).withArguments(password).thenReturn(credential);

        final Subject subject = new Subject();

        final Server server = mock(Server.class);
        when(server.getUsername()).thenReturn(username);
        when(server.getPassword()).thenReturn(encryptedPassword);

        final SecDispatcher dispatcher = mock(SecDispatcher.class);
        when(dispatcher.decrypt(encryptedPassword)).thenReturn(password);

        final Settings settings = mock(Settings.class);
        when(settings.getServer(serverId)).thenReturn(server);

        mojo.settings = settings;
        mojo.frontendCredentialsId = serverId;
        mojo.domain = domainName;
        mojo.securityDispatcher = dispatcher;

        mojo.addDomainLoginInformation(subject);

        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);
    }

    /**
     * If no server exists by the given ID, then adding the domain login information should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationWithServerIdNoSuchServer() throws Exception {
        final String serverId = "a.server.id";
        final Settings settings = mock(Settings.class);

        mojo.domain = "a.domain.name";
        mojo.frontendCredentialsId = serverId;
        mojo.settings = settings;

        expected.expect(MojoExecutionException.class);
        expected.expectMessage("No frontend <server /> found by the given ID: ");
        mojo.addDomainLoginInformation(new Subject());
    }

    /**
     * Even if a domain username and password are set, a frontend credentials ID should be given priority.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDomainLoginInformationWithServerIdPrioritized() throws Exception {
        final String serverId = "a.server.id";
        final String domainName = "a.domain.name";
        final String username = "a.domain.username";
        final String password = "a.domain.password";
        final String encryptedPassword = StringUtils.reverse(password);

        final MillenniumDomainPrincipal principal = mock(MillenniumDomainPrincipal.class);
        whenNew(MillenniumDomainPrincipal.class).withArguments(username, domainName).thenReturn(principal);

        final MillenniumDomainPasswordCredential credential = mock(MillenniumDomainPasswordCredential.class);
        whenNew(MillenniumDomainPasswordCredential.class).withArguments(password).thenReturn(credential);

        final Subject subject = new Subject();

        final Server server = mock(Server.class);
        when(server.getUsername()).thenReturn(username);
        when(server.getPassword()).thenReturn(encryptedPassword);

        final SecDispatcher dispatcher = mock(SecDispatcher.class);
        when(dispatcher.decrypt(encryptedPassword)).thenReturn(password);

        final Settings settings = mock(Settings.class);
        when(settings.getServer(serverId)).thenReturn(server);

        final Log log = mock(Log.class);

        mojo.setLog(log);
        mojo.domainUsername = username + "-unused";
        mojo.settings = settings;
        mojo.frontendCredentialsId = serverId;
        mojo.domain = domainName;
        mojo.securityDispatcher = dispatcher;

        mojo.addDomainLoginInformation(subject);

        assertThat(subject.getPrincipals()).containsOnly(principal);
        assertThat(subject.getPrivateCredentials()).containsOnly(credential);

        verify(log).warn(
                "A frontend username or password was provided as well as a credentials ID; the ID will be used over the username and password.");
    }

    /**
     * Verify that the CCL session output is set to be forwarded.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableCclDebugging() throws Exception {
        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final Log log = mock(Log.class);
        when(log.isDebugEnabled()).thenReturn(Boolean.TRUE);

        final ArgumentCaptor<OutputStream> streamCaptor = ArgumentCaptor.forClass(OutputStream.class);

        mojo.logFile = new File("target/unit/ccl.log");
        mojo.setLog(log);
        mojo.createCclExecutor();

        verify(executor).setOutputStream(streamCaptor.capture(), eq(OutputType.CCL_SESSION));
        final OutputStream captured = streamCaptor.getValue();
        assertThat(captured).isInstanceOf(DelegatingOutputStream.class);

        final DelegatingOutputStream delegate = (DelegatingOutputStream) captured;
        try {
            final Collection<OutputStream> delegateStreams = delegate.getStreams();
            assertThat(delegateStreams).hasSize(2);

            LogOutputStreamProxy proxy = null;
            CclLogFileOutputStream logStream = null;
            for (final OutputStream stream : delegateStreams)
                if (stream instanceof LogOutputStreamProxy)
                    proxy = (LogOutputStreamProxy) stream;
                else if (stream instanceof CclLogFileOutputStream)
                    logStream = (CclLogFileOutputStream) stream;

            assertThat(proxy).isNotNull();
            assertThat(proxy.getLogProxy().getLog()).isSameAs(log);

            assertThat(logStream).isNotNull();
        } finally {
            delegate.close();
        }
    }

    /**
     * Verify that, if debugging is not enabled, that the executor's output stream is not set.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableCclDebuggingNotDebug() throws Exception {
        final Log log = mock(Log.class);
        when(log.isDebugEnabled()).thenReturn(Boolean.FALSE);

        mojo.setLog(log);
        mojo.createCclExecutor();

        verify(executor, never()).setOutputStream(any(OutputStream.class), any(OutputType.class));
    }

    /**
     * Test the enabling of file log output.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableFileLogOutput() throws Exception {
        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final Log log = mock(Log.class);
        when(log.isDebugEnabled()).thenReturn(Boolean.TRUE);

        final ArgumentCaptor<OutputStream> streamCaptor = ArgumentCaptor.forClass(OutputStream.class);

        mojo.logFile = new File("target/unit/ccl.log");
        mojo.setLog(log);
        mojo.createCclExecutor();

        verify(executor).setOutputStream(streamCaptor.capture(), eq(OutputType.CCL_SESSION));
        final OutputStream captured = streamCaptor.getValue();
        assertThat(captured).isInstanceOf(DelegatingOutputStream.class);

        final DelegatingOutputStream delegate = (DelegatingOutputStream) captured;
        try {
            final Collection<OutputStream> delegateStreams = delegate.getStreams();
            assertThat(delegateStreams).isNotEmpty();

            CclLogFileOutputStream logStream = null;
            for (final OutputStream stream : delegateStreams)
                if (stream instanceof CclLogFileOutputStream)
                    logStream = (CclLogFileOutputStream) stream;

            assertThat(logStream).isNotNull();
        } finally {
            delegate.close();
        }
    }

    /**
     * If the mojo is set to do full debug, then it should enable full debug in the executor.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableFullDebug() throws Exception {
        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final ArgumentCaptor<OutputStream> streamCaptor = ArgumentCaptor.forClass(OutputStream.class);

        mojo.enableFullDebug = true;
        mojo.createCclExecutor();

        verify(executor).setOutputStream(streamCaptor.capture(), eq(OutputType.FULL_DEBUG));
        final OutputStream captured = streamCaptor.getValue();
        assertThat(captured).isInstanceOf(DelegatingOutputStream.class);

        final DelegatingOutputStream delegate = (DelegatingOutputStream) captured;
        try {
            final Collection<OutputStream> delegateStreams = delegate.getStreams();
            assertThat(delegateStreams).isNotEmpty();

            LogOutputStreamProxy logStream = null;
            for (final OutputStream stream : delegateStreams)
                if (stream instanceof LogOutputStreamProxy)
                    logStream = (LogOutputStreamProxy) stream;

            assertThat(logStream).isNotNull();
        } finally {
            delegate.close();
        }
    }

    /**
     * Even if debug mode is enabled within Maven, that should be overridden and full debug should be used instead.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableFullDebugOverridesMavenDebug() throws Exception {
        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final Log log = mock(Log.class);
        when(log.isDebugEnabled()).thenReturn(Boolean.TRUE);

        final ArgumentCaptor<OutputStream> streamCaptor = ArgumentCaptor.forClass(OutputStream.class);

        mojo.setLog(log);
        mojo.enableFullDebug = true;
        mojo.createCclExecutor();

        verify(executor).setOutputStream(streamCaptor.capture(), eq(OutputType.FULL_DEBUG));
        final OutputStream captured = streamCaptor.getValue();
        assertThat(captured).isInstanceOf(DelegatingOutputStream.class);

        final DelegatingOutputStream delegate = (DelegatingOutputStream) captured;
        try {
            final Collection<OutputStream> delegateStreams = delegate.getStreams();
            assertThat(delegateStreams).isNotEmpty();

            LogOutputStreamProxy logStream = null;
            for (final OutputStream stream : delegateStreams)
                if (stream instanceof LogOutputStreamProxy)
                    logStream = (LogOutputStreamProxy) stream;

            assertThat(logStream).isNotNull();
        } finally {
            delegate.close();
        }
    }

    /**
     * Verify that files are fetched, regardless of capitalization.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFilesCaseInsensitive() throws Exception {
        final File baseDir = new File("target/unit/testGetFilesCaseInsensitive/");
        FileUtils.deleteDirectory(baseDir);

        final File allUpperCase = new File(baseDir, "allUpperCase.PRG");
        final File allLowerCase = new File(baseDir, "allLowerCase.prg");

        // Verify that the files exist
        FileUtils.touch(allLowerCase);
        FileUtils.touch(allUpperCase);

        assertThat(mojo.getFiles(baseDir, ".*\\.prg")).containsOnly(allLowerCase, allUpperCase);
    }

    /**
     * Verify that, given a non-existent directory, the mojo will merely return an empty list.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFilesNonExistentDirectory() throws Exception {
        final File nonexistent = mock(File.class);
        when(nonexistent.exists()).thenReturn(Boolean.FALSE);

        assertThat(mojo.getFiles(nonexistent, "")).isEmpty();
    }

    /**
     * Test the retrieval of the subject.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetSubject() throws Exception {
        final Principal backendPrincipal = mock(Principal.class);
        final Principal frontendPrincipal = mock(Principal.class);

        final StubCclMojo toTest = new StubCclMojo() {
            @Override
            protected void addBackendInformation(final Subject subject) {
                subject.getPrincipals().add(backendPrincipal);
            }

            @Override
            protected void addDomainLoginInformation(final Subject subject) {
                subject.getPrincipals().add(frontendPrincipal);
            }
        };

        final Subject subject = toTest.getSubject();
        assertThat(subject.getPrincipals()).containsOnly(backendPrincipal, frontendPrincipal);
    }

    /**
     * Verify that the base mojo's indicating of debugging is driven by its {@link Log} object.
     */
    @Test
    public void testIsDebugging() {
        final Log log = mock(Log.class);
        mojo.setLog(log);

        when(log.isDebugEnabled()).thenReturn(Boolean.FALSE);
        assertThat(mojo.isDebugging()).isFalse();

        when(log.isDebugEnabled()).thenReturn(Boolean.TRUE);
        assertThat(mojo.isDebugging()).isTrue();
    }

    /**
     * A stub of {@link BaseCclMojo} for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class StubCclMojo extends BaseCclMojo {
        public StubCclMojo() {
        }

        public void execute() {
        }
    }
}
