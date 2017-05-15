package com.learningmachine.android.app;

import android.content.Context;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.Document;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.model.TxRecordOut;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.MainNetParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.SignatureException;
import java.util.List;

import rx.Observable;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example of certificate test. Should be renamed or moved to the correct class test.
 */
public class CertificateVerificationTest {

    public static final String BLOCKCHAIN_TX_RECORD_ID = "d3f042497b1469446e95a9e289f26c551083a3a94c10fbb9c848be327ebf620d";
    public static final String BLOCKCHAIN_TX_RECORD_FILENAME = "txrecord-" + BLOCKCHAIN_TX_RECORD_ID + ".json";
    public static final String CERT_ID = "8e02c2c4499e4e108b07ff5504438f4d";
    public static final String CERT_FILENAME = "certificate-" + CERT_ID + ".json";
    public static final String FORGED_CERT_FILENAME = "forged-cert-" + CERT_ID + ".json";
    public static final String ISSUER_FILENAME = "issuer-58ffaf130456e116107f68e6.json";

    private CertificateVerifier subject;
    private TxRecord mTxRecord;
    private IssuerResponse mIssuer;
    private Certificate validCertificate;
    private Certificate forgedCertificate;

    @Before
    public void setup() {
        Context context = Mockito.mock(Context.class);

        Gson gson = new Gson();

        BlockchainService blockchainService = mock(BlockchainService.class);
        mTxRecord = gson.fromJson(getResourceAsReader(BLOCKCHAIN_TX_RECORD_FILENAME), TxRecord.class);
        when(blockchainService.getBlockchain(any())).thenReturn(Observable.just(mTxRecord));

        IssuerService issuerService = mock(IssuerService.class);
        mIssuer = gson.fromJson(getResourceAsReader(ISSUER_FILENAME), IssuerResponse.class);
        when(issuerService.getIssuer(any())).thenReturn(Observable.just(mIssuer));

        subject = new CertificateVerifier(context, blockchainService, issuerService);

        validCertificate = gson.fromJson(getResourceAsReader(CERT_FILENAME), Certificate.class);
        forgedCertificate = gson.fromJson(getResourceAsReader(FORGED_CERT_FILENAME), Certificate.class);
    }

    @Test
    public void validCertificateShouldVerifyIssuer() {
        subject.verifyIssuer(validCertificate)
                .subscribe(issuerKey -> assertEquals(issuerKey, issuerKey));
    }

    @Test
    public void validCertificateShouldVerifyBitcoinTransaction() {
        subject.verifyBitcoinTransactionRecord(validCertificate)
                .subscribe(remoteHash -> assertEquals(remoteHash, remoteHash));
    }

    @Test
    public void forgedCertificateShouldFail() {
        // TODO: we need to do JSON-LD, which currently requires a WebView...
    }

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

        // Blockchain Transaction
        // get blockchain transaction record ID from certificate.signature.anchors[0].sourceId
        String txId = validCertificate.getReceipt().getFirstAnchorSourceId();

        // txId would now be used to download the blockchain transaction record
        assertThat(txId, equalTo(BLOCKCHAIN_TX_RECORD_ID));

        Sha256Hash localHash = Sha256Hash.of(ByteStreams.toByteArray(getResourceAsStream(CERT_FILENAME)));

        // download blockchain transaction record from https://blockchain.info/rawtx/<transaction_id>

        TxRecordOut lastOut = mTxRecord.getLastOut();
        int value = lastOut.getValue();
        String remoteHash = lastOut.getScript();

        assertThat(value, equalTo(0));

        // strip out 6a20 prefix, if present
        remoteHash = remoteHash.startsWith("6a20") ? remoteHash.substring(4) : remoteHash;

        assertThat(remoteHash, equalTo(validCertificate.getReceipt().getMerkleRoot()));

        // Issuer
        // get issuer from URL in certificate.badge.issuer.id

        assertThat(mIssuer.getIssuerKeys(), not(empty()));

        KeyRotation firstIssuerKey = mIssuer.getIssuerKeys().get(0);

        Document document = validCertificate.getDocument();
        String signature = document.getSignature();
        String uid = document.getAssertion().getUuid();

        ECKey ecKey = ECKey.signedMessageToKey(uid, signature);
        ecKey.verifyMessage(uid, signature); // this is tautological

        Address address = ecKey.toAddress(MainNetParams.get());
        assertEquals(firstIssuerKey.getKey(), address.toBase58());

        // Revocation
        List<KeyRotation> revocationKeys = mIssuer.getRevocationKeys();
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
