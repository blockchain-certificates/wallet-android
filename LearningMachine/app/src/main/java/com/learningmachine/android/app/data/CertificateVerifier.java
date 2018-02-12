package com.learningmachine.android.app.data;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.gson.JsonObject;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class CertificateVerifier {
    private static final String JSONLD_TEMP_FILE_NAME= "jsonld";
    private static final String JSONLD_TEMP_FILE_EXT= "html";
    private static final String VIEW_CERTIFICATE_PREFIX_FILE = "view-certificate-prefix.html";
    private static final String VIEW_CERTIFICATE_SUFFIX_FILE = "view-certificate-suffix.html";

    private final WebView mWebView;
    private final Context mContext;
    private final BlockchainService mBlockchainService;
    private final IssuerService mIssuerService;

    private BehaviorSubject<CertificateVerificationStatus> mUpdates;

    @Inject
    public CertificateVerifier(Context context, BlockchainService blockchainService, IssuerService issuerService) {
        mContext = context;
        mBlockchainService = blockchainService;
        mIssuerService = issuerService;
        mWebView = new WebView(mContext);

        mUpdates = BehaviorSubject.create();
    }

    public BehaviorSubject<CertificateVerificationStatus> getUpdates() {
        return mUpdates;
    }

    public Observable<BlockCert> loadCertificate(String certificateUuid) {
        return getCertificate(mContext, certificateUuid);
    }

    private Observable<BlockCert> getCertificate(Context context, String certificateUuid) {
        File file = FileUtils.getCertificateFile(context, certificateUuid);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            BlockCertParser blockCertParser = new BlockCertParser();
            BlockCert blockCert = blockCertParser.fromJson(inputStream);
            return Observable.just(blockCert);
        } catch (IOException | NoSuchElementException e) {
            Timber.e(e, "Could not read certificate file");
            return Observable.error(new ExceptionWithResourceString(e, R.string.error_cannot_load_certificate_json));
        }
    }

    public Observable<TxRecord> verifyBitcoinTransactionRecord(BlockCert certificate) {
        String receiverKey = certificate.getRecipientPublicKey();
        String issuerKey = certificate.getVerificationPublicKey();

        // testnet check: if either the receiver or the issuer originated
        // from testnet then we will not validate using the normal method
        if(receiverKey != null && issuerKey != null) {
            if(receiverKey.startsWith("m") || receiverKey.startsWith("n") || issuerKey.startsWith("m") || issuerKey.startsWith("n")) {
                Timber.e("This is a testnet certificate and cannot be verified");
                return Observable.error(new ExceptionWithResourceString(R.string.error_testnet_certificate_json));
            }
        }

        mUpdates.onNext(CertificateVerificationStatus.CHECKING_MERKLE);
        String sourceId = certificate.getSourceId();
        return mBlockchainService.getBlockchain(sourceId).delay(2, TimeUnit.SECONDS)
                .flatMap(txRecord -> verifyBitcoinTransactionRecord(certificate, txRecord));
    }

    private Observable<TxRecord> verifyBitcoinTransactionRecord(BlockCert certificate, TxRecord txRecord) {
        String merkleRoot = certificate.getMerkleRoot();
        String remoteHash = txRecord.getRemoteHash();

        if (remoteHash == null || !remoteHash.equals(merkleRoot)) {
            Timber.e("The transaction record hash doesn't match the certificate's Merkle root");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_merkle_root));
        }

        Timber.d("Blockchain transaction is downloaded successfully");
        return Observable.just(txRecord).delay(2, TimeUnit.SECONDS);
    }

    public Observable<IssuerResponse> verifyIssuer(BlockCert certificate, TxRecord txRecord) {
        mUpdates.onNext(CertificateVerificationStatus.CHECKING_AUTHENTICITY);
        String issuerId = certificate.getIssuerId();
        return mIssuerService.getIssuer(issuerId).delay(2, TimeUnit.SECONDS)
                .flatMap(issuerResponse -> verifyIssuer(issuerResponse, txRecord));
    }

    private Observable<IssuerResponse> verifyIssuer(IssuerResponse issuerResponse, TxRecord txRecord) {
        boolean addressVerified = issuerResponse.verifyTransaction(txRecord);
        if (!addressVerified) {
            Timber.e("The issuer key doesn't match the certificate address");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_issuer_doesnt_match_address));
        }
        return Observable.just(issuerResponse);
    }

    public Observable<String> verifyJsonLd(BlockCert certificate, TxRecord txRecord) {
        mUpdates.onNext(CertificateVerificationStatus.COMPARING_HASHES);
        String possibleHash = certificate.getReceiptHash();
        if (possibleHash == null) {
            possibleHash = txRecord.getRemoteHash();
        }
        final String remoteHash = possibleHash;
        JsonObject documentNode = certificate.getDocumentNode();
        if (documentNode == null) {

            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
        Handler handler = new Handler(Looper.getMainLooper());
        return Observable.create(emitter -> {
            String serializedDoc = documentNode.toString();
            Timber.i(serializedDoc);
            File certsDir = new File(mContext.getFilesDir(), "certs");
            File file = null;
            try {
                file = File.createTempFile(JSONLD_TEMP_FILE_NAME, JSONLD_TEMP_FILE_EXT, certsDir);
            } catch (IOException e) {
                Timber.e(e, "Couldn't create a temp file for JSONLD normalization");
                emitter.onError(e);
                return;
            }

            try (InputStream prefixInputStream = mContext.getAssets().open(VIEW_CERTIFICATE_PREFIX_FILE);
                 InputStream suffixInputStream = mContext.getAssets().open(VIEW_CERTIFICATE_SUFFIX_FILE)) {
                FileUtils.appendCharactersToFile(prefixInputStream, file);
                FileUtils.appendStringToFile(serializedDoc, file);
                FileUtils.appendCharactersToFile(suffixInputStream, file);
            } catch (Exception e) {
                Timber.e(e, "Couldn't save the certificate document node");
                emitter.onError(e);
                return;
            }
            HashComparison jsonldCallback = new HashComparison(emitter, remoteHash);
            File finalFile = file;
            handler.post(() -> configureWebView(serializedDoc, finalFile, jsonldCallback));
        }, Emitter.BackpressureMode.DROP);
    }

    private void configureWebView(String serializedDoc, File file, HashComparison jsonldCallback) {
        mWebView.addJavascriptInterface(jsonldCallback, "jsonldCallback");
        WebSettings webSettings = mWebView.getSettings();
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///" + file.getAbsolutePath());
    }

    private static class HashComparison {
        private final Emitter<String> mEmitter;
        private final String mRemoteHash;

        public HashComparison(Emitter<String> emitter, String remoteHash) {
            mEmitter = emitter;
            mRemoteHash = remoteHash;
        }

        @JavascriptInterface
        public void result(Object error, String normalizedJsonld) {
            if (error != null) {
                Exception e = new ExceptionWithResourceString(R.string.error_invalid_certificate_normalize_json);
                Timber.e(e, "Could not normalize JSON-LD");
                mEmitter.onError(e);
                return;
            }
            Timber.d("Got the callback!");
            Timber.d(normalizedJsonld);
            MessageDigest sha256 = null;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                Timber.e(e, "Couldn't obtain SHA-256, the impossible situation");
                mEmitter.onError(e);
                return;
            }
            byte[] localHashBytes = sha256.digest(normalizedJsonld.getBytes());
            String localHash = String.format("%032x", new BigInteger(1, localHashBytes));
            if (localHash.equals(mRemoteHash)) {
                mEmitter.onNext(localHash);
            } else {
                Exception e = new ExceptionWithResourceString(R.string.error_remote_and_local_hash_mismatch);
                Timber.e(e, String.format("Remote hash [%s] does not match local hash [%s]", mRemoteHash, localHash));
                mEmitter.onError(e);
            }
        }
    }

    public enum CertificateVerificationStatus {
        // unused for now
//        COMPUTING_LOCAL_HASH(1, R.string.cert_verification_step_computing_local_hash),
//        FETCHING_REMOTE_HASH(2, R.string.cert_verification_step_fetching_remote_hash),
//        CHECKING_RECEIPT(5, R.string.cert_verification_step_checking_receipt),
//        CHECKING_ISSUER_SIGNATURE(7, R.string.cert_verification_step_checking_issuer_signature),
//        CHECKING_CERT_STATUS(8, R.string.cert_verification_step_checking_cert_status),
        CHECKING_MERKLE(1, R.string.cert_verification_step_checking_merkle),
        CHECKING_AUTHENTICITY(2, R.string.cert_verification_step_checking_authenticity),
        COMPARING_HASHES(3, R.string.cert_verification_step_comparing_hashes);

        private int mStepNumber;
        private int mMessageResId;

        CertificateVerificationStatus(int stepNumber, int messageResId) {
            mStepNumber = stepNumber;
            mMessageResId = messageResId;
        }

        public int getStepNumber() {
            return mStepNumber;
        }

        public int getMessageResId() {
            return mMessageResId;
        }

        public static int getTotalSteps() {
            return CertificateVerificationStatus.values().length;
        }
    }
}
