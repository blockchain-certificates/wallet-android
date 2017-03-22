package com.learningmachine.android.app;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.utils.BriefLogFormatter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void testCertificateVerification() throws Exception {
        String certSignature = "H0osFKllW8LrBhNMc4gC0TbRU0OK9Qgpebji1PgmNsgtSKCLXHL217cEG3FoHkaF/G2woGaoKDV/MrmpROvD860=";
        String assertionUid = "609c2989-275f-4f4c-ab02-b245cfb09017";

        ECKey ecKey = ECKey.signedMessageToKey(assertionUid, certSignature);
        ecKey.verifyMessage(assertionUid, certSignature);
        NetworkParameters networkParameters = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);

        if (networkParameters == null) {
            return;
        }

        Address address = ecKey.toAddress(networkParameters);
        String issuerKey = "1Q3P94rdNyftFBEKiN1fxmt2HnQgSCB619";
        assertEquals(issuerKey, address.toBase58());
    }
}