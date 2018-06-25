package com.cerner.ccl.testing.maven.ccl.mojo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.util.Collections;

import javax.security.auth.Subject;

import org.apache.maven.model.Resource;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.util.CclResourceUploader;
import com.cerner.ccl.testing.maven.ccl.mojo.BaseCclResourceMojo;
import com.cerner.ccl.testing.maven.ccl.mojo.ResourcesMojo;

/**
 * Unit tests for {@link BaseCclResourceMojo}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { BaseCclResourceMojo.class, CclResourceUploader.class })
public class BaseCclResourceMojoTest {
    /**
     * Verify the "uploading" of files.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecute() throws Exception {
        // Create the files to be "uploaded"
        final File directory = new File("target/unit/src/main/resources/");
        FileUtils.mkdir(directory.getAbsolutePath());

        final File testFile = new File(directory, "test.txt");
        testFile.createNewFile();

        final File testInc = new File(directory, "test.inc");
        testInc.createNewFile();

        final CclResourceUploader uploader = mock(CclResourceUploader.class);
        mockStatic(CclResourceUploader.class);
        when(CclResourceUploader.getUploader()).thenReturn(uploader);

        final Resource resource = mock(Resource.class);
        when(resource.getDirectory()).thenReturn(directory.getAbsolutePath());

        final StubMojo mojo = new StubMojo();
        mojo.setSubject(new Subject());
        mojo.upload(Collections.singletonList(resource));

        verify(uploader).queueUpload(testFile.getAbsoluteFile());
        verify(uploader).queueUpload(testInc.getAbsoluteFile());
        verify(uploader).upload();
        verifyNoMoreInteractions(uploader);
    }

    /**
     * A stub of {@link ResourcesMojo}.
     *
     * @author Joshua Hyde
     *
     */
    private static class StubMojo extends BaseCclResourceMojo {
        private Subject subject;

        public StubMojo() {
        }

        public void execute() {
        }

        /**
         * Set the subject to be used by this mojo.
         *
         * @param subject
         *            The {@link Subject} to be used by this mojo.
         */
        public void setSubject(final Subject subject) {
            this.subject = subject;
        }

        @Override
        protected Subject getSubject() {
            return subject;
        }
    }
}
