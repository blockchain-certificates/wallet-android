package com.learningmachine.android.app.data.cert.v20;

import com.learningmachine.android.app.data.cert.BlockCert;

import com.learningmachine.android.test.helpers.BlockCertHelpers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockCertV20Test {
    @Test
    public void testGetCertUid () {
        final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v2/mainnet-valid.json");
        assertEquals("https://blockcerts.learningmachine.com/certificate/5c447c71572d518e9dad57c4e91f9929", blockCert.getCertUid());
    }
}
