package com.learningmachine.android.app;

import android.content.Context;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.learningmachine.android.app.data.CertificateVerifier;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example of certificate test. Should be renamed or moved to the correct class test.
 */
public class CertificateVerificationTest {
    public static final String COMMON_CERTIFICATE_PATH = "common/Certificates";
    public static final String V20_ALPHA_CERTIFICATE_PATH = COMMON_CERTIFICATE_PATH + "/v2.0-alpha";
    public static final String V20_CERTIFICATE_PATH = COMMON_CERTIFICATE_PATH + "/v2.0";

    public static final String BTC_TX_RECORD_ID_D3F042 = "d3f042497b1469446e95a9e289f26c551083a3a94c10fbb9c848be327ebf620d";
    public static final String BTC_TX_RECORD_D3F042_FILENAME = "txrecord-" + BTC_TX_RECORD_ID_D3F042 + ".json";
    public static final String BTC_TX_RECORD_ID_C7667D = "c7667d47db19423952005df21474045af2bef675de2c13bf7f34bc64cfa3c114";
    public static final String BTC_TX_RECORD_C7667D_FILENAME = "txrecord-" + BTC_TX_RECORD_ID_C7667D + ".json";
    public static final String BTC_TX_RECORD_ID_A05E8B = "a05e8b7998c14820036fae46eb3c4e698433db8036114ef62fcc8ab5850b5dea";
    public static final String BTC_TX_RECORD_MAINNET_20_FILENAME = "txrecord-" + BTC_TX_RECORD_ID_A05E8B + ".json";
    public static final String CERT_ID = "8e02c2c4499e4e108b07ff5504438f4d";
    public static final String CERT_FILENAME = V20_ALPHA_CERTIFICATE_PATH + "/certificate-" + CERT_ID + ".json";
    public static final String CERT_V20_ALPHA_FILENAME = V20_ALPHA_CERTIFICATE_PATH + "/mainnet-bolot.json";
    public static final String CERT_V20_FILENAME = V20_CERTIFICATE_PATH + "/mainnet.json";
    public static final String FORGED_CERT_FILENAME = "forged-cert-" + CERT_ID + ".json";

    public static final String ISSUER_FILENAME = "issuer-v2.json";

    private CertificateVerifier subject;
    private TxRecord mTxRecordD3F042;
    private TxRecord mTxRecordC7667D;
    private TxRecord mTxRecordA05E8B;
    private IssuerResponse mIssuer;
    private BlockCert validCertV12;
    private BlockCert validCertV20alpha;
    private BlockCert validCertV20;
    private BlockCert forgedCertificate;

    @Before
    public void setup() {
        Context context = Mockito.mock(Context.class);

        Gson gson = new Gson();

        BlockchainService blockchainService = mock(BlockchainService.class);
        mTxRecordC7667D = gson.fromJson(getResourceAsReader(BTC_TX_RECORD_C7667D_FILENAME), TxRecord.class);
        mTxRecordD3F042 = gson.fromJson(getResourceAsReader(BTC_TX_RECORD_D3F042_FILENAME), TxRecord.class);
        mTxRecordA05E8B = gson.fromJson(getResourceAsReader(BTC_TX_RECORD_MAINNET_20_FILENAME), TxRecord.class);
        when(blockchainService.getBlockchain(BTC_TX_RECORD_ID_C7667D)).thenReturn(Observable.just(mTxRecordC7667D));
        when(blockchainService.getBlockchain(BTC_TX_RECORD_ID_D3F042)).thenReturn(Observable.just(mTxRecordD3F042));
        when(blockchainService.getBlockchain(BTC_TX_RECORD_ID_A05E8B)).thenReturn(Observable.just(mTxRecordA05E8B));

        IssuerService issuerService = mock(IssuerService.class);
        mIssuer = gson.fromJson(getResourceAsReader(ISSUER_FILENAME), IssuerResponse.class);
        when(issuerService.getIssuer(any())).thenReturn(Observable.just(mIssuer));

        subject = new CertificateVerifier(context, blockchainService, issuerService);

        BlockCertParser blockCertParser = new BlockCertParser();
        validCertV12 = blockCertParser.fromJson(getResourceAsStream(CERT_FILENAME));
        validCertV20alpha = blockCertParser.fromJson(getResourceAsStream(CERT_V20_ALPHA_FILENAME));
        validCertV20 = blockCertParser.fromJson(getResourceAsStream(CERT_V20_FILENAME));
        forgedCertificate = blockCertParser.fromJson(getResourceAsStream(FORGED_CERT_FILENAME));
    }

    @Test
    public void validCertV12ShouldVerifyIssuer() {
        subject.verifyIssuer(validCertV12, mTxRecordC7667D)
                .subscribe(issuerKey -> assertTrue(true));
    }

    @Test
    public void validCertV12ShouldVerifyBitcoinTransaction() {
        subject.verifyBitcoinTransactionRecord(validCertV12)
                .subscribe(txRecord -> assertTrue(true));
    }

    @Test
    public void validCertV20AlphaShouldVerifyIssuer() {
        subject.verifyIssuer(validCertV20alpha, mTxRecordD3F042)
                .subscribe(issuerKey -> assertTrue(true));
    }

    @Test
    public void validCertV20AlphaShouldVerifyBitcoinTransaction() {
        subject.verifyBitcoinTransactionRecord(validCertV20alpha)
                .subscribe(remoteHash -> assertTrue(true));
    }

    @Test
    public void validCertV20ShouldVerifyIssuer() {
        subject.verifyIssuer(validCertV20, mTxRecordD3F042)
                .subscribe(issuerKey -> assertTrue(true));
    }

    @Test
    public void validCertV20ShouldVerifyBitcoinTransaction() {
        subject.verifyBitcoinTransactionRecord(validCertV20)
                .subscribe(remoteHash -> assertTrue(true));
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
        String txId = validCertV12.getSourceId();

        // txId would now be used to download the blockchain transaction record
        assertThat(txId, equalTo(BTC_TX_RECORD_ID_D3F042));

        Sha256Hash localHash = Sha256Hash.of(ByteStreams.toByteArray(getResourceAsStream(CERT_FILENAME)));

        // download blockchain transaction record from https://blockchain.info/rawtx/<transaction_id>

        TxRecordOut lastOut = mTxRecordD3F042.getLastOut();
        int value = lastOut.getValue();
        String remoteHash = lastOut.getScript();

        assertThat(value, equalTo(0));

        // strip out 6a20 prefix, if present
        remoteHash = remoteHash.startsWith("6a20") ? remoteHash.substring(4) : remoteHash;

        assertThat(remoteHash, equalTo(validCertV12.getMerkleRoot()));

        // Issuer
        // get issuer from URL in certificate.badge.issuer.id

        assertThat(mIssuer.getIssuerKeys(), not(empty()));

//        String address = validCertV12.getAddress(MainNetParams.get());
//        assertTrue("Address is supposed to match the issuer", mIssuer.verifyAddress(address));

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
