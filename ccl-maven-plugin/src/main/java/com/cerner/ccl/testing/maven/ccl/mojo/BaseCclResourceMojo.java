package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;

import com.cerner.ccl.j4ccl.util.CclResourceUploader;

/**
 * Basic implementation of {@link BaseCclMojo} to provide common functionality for processing of resources.
 *
 * @author Joshua Hyde
 *
 */

public abstract class BaseCclResourceMojo extends BaseCclMojo {
    /**
     * Upload all files within a given set of resources.
     *
     * @param resources
     *            A {@link Collection} of objects that are or extend {@link Resource} representing the directories whose
     *            contents will be uploaded.
     * @throws MojoExecutionException
     *             If any errors occur in creating the uploader.
     */
    protected final void upload(final Collection<? extends Resource> resources) throws MojoExecutionException {
        if (resources.isEmpty()) {
            return;
        }

        try {
            final CclResourceUploader uploader = CclResourceUploader.getUploader();
            for (final Resource resource : resources) {
                if (FileUtils.fileExists(resource.getDirectory())) {
                    List<File> files;
                    try {
                        files = FileUtils.getFiles(FileUtils.getFile(resource.getDirectory()), "*", null);
                    } catch (final IOException e) {
                        throw new MojoExecutionException("Failed to look for files for uploading.", e);
                    }
                    for (final File file : files) {
                        uploader.queueUpload(file);
                    }
                }
            }

            Subject.doAs(getSubject(), new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    uploader.upload();
                    return null;
                }
            });
        } catch (final PrivilegedActionException e) {
            throw new MojoExecutionException("Failed to upload all files.", e);
        }
    }
}
