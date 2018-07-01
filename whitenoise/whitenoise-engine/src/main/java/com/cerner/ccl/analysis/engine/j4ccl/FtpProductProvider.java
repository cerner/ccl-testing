package com.cerner.ccl.analysis.engine.j4ccl;

import com.cerner.ftp.data.FtpProduct;

/**
 * Definition of an object used to provision an {@link FtpProduct} to be used upload and download files from an
 * FTP-capable server.
 * 
 * @author Joshua Hyde
 * 
 */

public interface FtpProductProvider {
    /**
     * Get the product.
     * 
     * @return An {@link FtpProduct} to be used to provision credentials used to authenticate while downloading and
     *         uploading files.
     */
    FtpProduct getProduct();
}
