package com.cerner.ftp.sftp.jsch;

import com.jcraft.jsch.SftpProgressMonitor;

/**
 * A progress monitor that does not do anything with its input (much like outputting to /dev/null on Linux and UNIX
 * systems - hence, the name).
 *
 * @author Joshua Hyde
 *
 */

public class DevNullMonitor implements SftpProgressMonitor {

    public boolean count(final long count) {
        return false;
    }

    public void end() {

    }

    public void init(final int op, final String src, final String dest, final long max) {

    }

}
