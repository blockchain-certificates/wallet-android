package com.learningmachine.android.app;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.Document;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.model.TxRecordOut;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.SignatureException;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Example of certificate test. Should be renamed or moved to the correct class test.
 */
public class CertificateVerificationTest {

    public static final String BLOCKCHAIN_TX_RECORD_ID = "d3f042497b1469446e95a9e289f26c551083a3a94c10fbb9c848be327ebf620d";
    public static final String BLOCKCHAIN_TX_RECORD_FILENAME = "txrecord-" + BLOCKCHAIN_TX_RECORD_ID + ".json";
    public static final String CERT_FILENAME = "certificate-8e02c2c4499e4e108b07ff5504438f4d.json";
    public static final String ISSUER_FILENAME = "issuer-58ffaf130456e116107f68e6.json";

    @Test
    public void testCertificateVerification() throws Exception {
        String certSignature = "H0osFKllW8LrBhNMc4gC0TbRU0OK9Qgpebji1PgmNsgtSKCLXHL217cEG3FoHkaF/G2woGaoKDV/MrmpROvD860=";
        String assertionUid = "609c2989-275f-4f4c-ab02-b245cfb09017";

        ECKey ecKey = ECKey.signedMessageToKey(assertionUid, certSignature);
        ecKey.verifyMessage(assertionUid, certSignature);

        Address address = ecKey.toAddress(MainNetParams.get());
        String issuerKey = "1Q3P94rdNyftFBEKiN1fxmt2HnQgSCB619";
        assertEquals(issuerKey, address.toBase58());
    }

    @Test
    public void testGetCertificateAndBlockchainTransaction() throws IOException, SignatureException {
        Gson gson = new Gson();

        // Inputs

        // Blockchain Certificate
        Reader in = getResourceAsReader(CERT_FILENAME);
        Certificate certificate = gson.fromJson(in, Certificate.class);

        // Blockchain Transaction
        // get blockchain transaction record ID from certificate.signature.anchors[0].sourceId
        String txId = certificate.getReceipt().getFirstAnchorSourceId();

        // txId would now be used to download the blockchain transaction record
        assertThat(txId, equalTo(BLOCKCHAIN_TX_RECORD_ID));

        Sha256Hash localHash = Sha256Hash.of(ByteStreams.toByteArray(getResourceAsStream(CERT_FILENAME)));

        // download blockchain transaction record from https://blockchain.info/rawtx/<transaction_id>
        Reader txRecordReader = getResourceAsReader(BLOCKCHAIN_TX_RECORD_FILENAME);
        TxRecord txRecord = gson.fromJson(txRecordReader, TxRecord.class);

        TxRecordOut lastOut = txRecord.getLastOut();
        int value = lastOut.getValue();
        String remoteHash = lastOut.getScript();

        assertThat(value, equalTo(0));

        // strip out 6a20 prefix, if present
        remoteHash = remoteHash.startsWith("6a20") ? remoteHash.substring(4) : remoteHash;

        assertThat(remoteHash, equalTo(certificate.getReceipt().getMerkleRoot()));

        // Issuer
        // get issuer from URL in certificate.badge.issuer.id
        Reader issuerReader = getResourceAsReader(ISSUER_FILENAME);
        Issuer issuer = gson.fromJson(issuerReader, Issuer.class);

        assertThat(issuer.getIssuerKeys(), not(empty()));

        KeyRotation firstIssuerKey = issuer.getIssuerKeys().get(0);

        Document document = certificate.getDocument();
        String signature = document.getSignature();
        String uid = document.getAssertion().getUuid();

        ECKey ecKey = ECKey.signedMessageToKey(uid, signature);
        ecKey.verifyMessage(uid, signature); // this is tautological

        Address address = ecKey.toAddress(MainNetParams.get());
        assertEquals(firstIssuerKey.getKey(), address.toBase58());

        // Revocation
        List<KeyRotation> revocationKeys = issuer.getRevocationKeys();
        // TODO: check certificate against revocations

        // TODO: JSON-LD canonicalization
    }

    private Reader getResourceAsReader(String name) {
        InputStream inputStream = getResourceAsStream(name);
        Reader reader = new InputStreamReader(inputStream);
        return reader;
    }

    private InputStream getResourceAsStream(String name) {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(name);
        return inputStream;
    }
}
