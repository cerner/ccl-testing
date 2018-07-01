package com.cerner.ccl.analysis.mojo.util;

import com.cerner.ccl.analysis.engine.j4ccl.FtpProductProvider;
import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ftp.data.FtpProduct;

/**
 * An {@link FtpProductProvider} that leverages utilities provided by the {@code j4ccl-ssh} to construct an
 * {@link FtpProduct}.
 * 
 * @author Joshua Hyde
 * 
 */

public class J4CclFtpProductProvider implements FtpProductProvider {
    /**
     * {@inheritDoc}
     */
    public FtpProduct getProduct() {
        return AuthHelper.fromCurrentSubject();
    }

}
