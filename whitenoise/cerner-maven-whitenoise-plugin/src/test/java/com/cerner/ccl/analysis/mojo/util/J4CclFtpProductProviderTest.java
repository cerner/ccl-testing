package com.cerner.ccl.analysis.mojo.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ftp.data.FtpProduct;

/**
 * Unit tests for {@link J4CclFtpProductProvider}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AuthHelper.class })
public class J4CclFtpProductProviderTest {
    private final J4CclFtpProductProvider provider = new J4CclFtpProductProvider();

    /**
     * Test the retrieval of the {@link FtpProduct}.
     */
    @Test
    public void testGetProduct() {
        final FtpProduct product = mock(FtpProduct.class);
        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(product);

        assertThat(provider.getProduct()).isEqualTo(product);
    }
}
