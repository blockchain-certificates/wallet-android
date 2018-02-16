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
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.cert.v20.MerkleProof2017Schema;
import com.learningmachine.android.app.data.cert.v20.Proof;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.FileUtils;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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
import java.util.Arrays;
import java.util.List;
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

    private final int delayTime = 1;

    @Inject
    public CertificateVerifier(Context context, BlockchainService blockchainService, IssuerService issuerService) {
        mContext = context;
        mBlockchainService = blockchainService;
        mIssuerService = issuerService;
        mWebView = new WebView(mContext);
    }

    public Observable<BlockCert> loadCertificate(String certificateUuid) {
        File file = FileUtils.getCertificateFile(mContext, certificateUuid);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            BlockCertParser blockCertParser = new BlockCertParser();
            BlockCert blockCert = blockCertParser.fromJson(inputStream);
            return Observable.just(blockCert);
        } catch (IOException | NoSuchElementException e) {
            Timber.e(e, "Could not read certificate file");
            return Observable.error(new ExceptionWithResourceString(e, R.string.error_cannot_load_certificate_json));
        }
    }

    public Observable<TxRecord> loadTXRecord(BlockCert certificate) {

        String receiverKey = certificate.getRecipientPublicKey();
        String issuerKey = certificate.getVerificationPublicKey();

        // TODO: REMOVE THIS AND ALLOW ACTUAL VERIFICATIONS OF CROSS CHAIN CERTIFICATES
        // testnet check: if either the receiver or the issuer originated
        // from testnet then we will not validate using the normal method
        if(receiverKey != null && issuerKey != null) {
            if(receiverKey.startsWith("m") || receiverKey.startsWith("n") || issuerKey.startsWith("m") || issuerKey.startsWith("n")) {
                Timber.e("loadCertificate - failed");
                return Observable.error(new ExceptionWithResourceString(R.string.error_testnet_certificate_json));
            }
        }

        String sourceId = certificate.getSourceId();
        return mBlockchainService.getBlockchain(sourceId).delay(delayTime, TimeUnit.SECONDS).flatMap((txRecord) -> {
            return Observable.just(txRecord);
        });
    }

    public Observable<Object> CompareComputedHashWithExpectedHash(BlockCert certificate, TxRecord txRecord) {

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
                Timber.e("CompareComputedHashWithExpectedHash - failed");
                emitter.onError(e);
                return;
            }

            try (InputStream prefixInputStream = mContext.getAssets().open(VIEW_CERTIFICATE_PREFIX_FILE);
                 InputStream suffixInputStream = mContext.getAssets().open(VIEW_CERTIFICATE_SUFFIX_FILE)) {
                FileUtils.appendCharactersToFile(prefixInputStream, file);
                FileUtils.appendStringToFile(serializedDoc, file);
                FileUtils.appendCharactersToFile(suffixInputStream, file);
            } catch (Exception e) {
                Timber.e("CompareComputedHashWithExpectedHash - failed");
                emitter.onError(e);
                return;
            }
            HashComparison jsonldCallback = new HashComparison(emitter, remoteHash);
            File finalFile = file;
            handler.post(() -> configureWebView(serializedDoc, finalFile, jsonldCallback));
        }, Emitter.BackpressureMode.DROP).delay(delayTime, TimeUnit.SECONDS);
    }

    public Observable<Object> EnsuringMerkleReceiptIsValid(BlockCert certificate, TxRecord txRecord) {

        return Observable.create(emitter -> {

            boolean hasValidReceipt = false;

            if (certificate instanceof BlockCertV20) {
                BlockCertV20 certV20 = (BlockCertV20) certificate;

                MerkleProof2017Schema signature = certV20.getSignature();

                MessageDigest sha256 = null;
                try {
                    sha256 = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    Timber.e(e, "Couldn't obtain SHA-256, the impossible situation");
                    emitter.onError(new ExceptionWithResourceString(R.string.error_step2_reason));
                    return;
                }

                if (signature != null && signature.getProof() != null) {

                    byte[] targetHash = StringUtils.asHexData(signature.getTargetHash());
                    byte[] merkleRoot = StringUtils.asHexData(signature.getMerkleRoot());

                    byte[] proofHash = targetHash;

                    // no proofs to combine, so we test that the two hashes match
                    if (signature.getProof().size() == 0) {
                        hasValidReceipt = Arrays.equals(targetHash, merkleRoot);
                    } else {
                        for (Proof p : signature.getProof()) {

                            if (p.getLeft() != null) {
                                proofHash = StringUtils.combineByteArrays(StringUtils.asHexData(p.getLeft()), proofHash);
                                proofHash = sha256.digest(proofHash);
                            } else if (p.getRight() != null) {
                                proofHash = StringUtils.combineByteArrays(proofHash, StringUtils.asHexData(p.getRight()));
                                proofHash = sha256.digest(proofHash);
                            } else {
                                // Either:
                                // 1. There's no left or right key.
                                // 2. There is, but it's not properly formatted hex data.
                                // In either case, we can't correctly compute the hash. This receipt is clearly
                                emitter.onError(new ExceptionWithResourceString(R.string.error_step2_reason));
                                return;
                            }
                        }
                        hasValidReceipt = Arrays.equals(proofHash, merkleRoot);
                    }

                } else {
                    hasValidReceipt = false;
                }

            } else {
                // certificates older than v2.0 we're not supporting, so pass them along
                hasValidReceipt = true;
            }

            if (!hasValidReceipt) {
                emitter.onError(new ExceptionWithResourceString(R.string.error_step2_reason));
                return;
            }

            Timber.d("EnsuringMerkleReceiptIsValid - success");
            emitter.onNext("");
        }, Emitter.BackpressureMode.DROP).delay(delayTime, TimeUnit.SECONDS);
    }

    public Observable<Object> ComparingExpectedMerkleRootWithValueOnTheBlockchain(BlockCert certificate, TxRecord txRecord) {
        return Observable.create(emitter -> {
            String merkleRoot = certificate.getMerkleRoot();
            String remoteHash = txRecord.getRemoteHash();

            if (remoteHash == null || !remoteHash.equals(merkleRoot)) {
                Timber.e("ComparingExpectedMerkleRootWithValueOnTheBlockchain - failed");
                emitter.onError(new ExceptionWithResourceString(R.string.error_step3_reason));
                return;
            }

            Timber.d("ComparingExpectedMerkleRootWithValueOnTheBlockchain - success");
            emitter.onNext("");
        }, Emitter.BackpressureMode.DROP).delay(delayTime, TimeUnit.SECONDS);

    }

    public Observable<IssuerResponse> ValidatingIssuerIdentity(BlockCert certificate, TxRecord txRecord) {
        String issuerId = certificate.getIssuerId();
        return mIssuerService.getIssuer(issuerId).delay(delayTime, TimeUnit.SECONDS)
                .flatMap(issuerResponse -> {

                    KeyRotation.KeyStatus status = issuerResponse.verifyTransaction(txRecord);
                    if (status != KeyRotation.KeyStatus.KEY_VALID) {
                        Timber.e("ValidatingIssuerIdentity - failed");
                        return Observable.error(new ExceptionWithResourceString(R.string.error_step4_reason));
                    }

                    Timber.d("ValidatingIssuerIdentity - success");
                    return Observable.just(issuerResponse);
                });
    }

    public Observable<Object> CheckingIfTheCredentialHasBeenRevoked(BlockCert certificate, TxRecord txRecord, IssuerResponse issuerResponse) {

        // TODO: Since we cannot test revocation of credentials yet, I'm going to leave this commented out

        /*
        // If the certificate has been revoked, the specific keys which were revoked are in the revoation keys list.
        // If the certificate id is contained in the revocation keys then it has been revoked by the issuer
        List<String> revocationList= issuerResponse.getRevocationList();
        if(revocationList != null && revocationList.contains(certificate.getUrl())) {
            Timber.d("CheckingIfTheCredentialHasBeenRevoked - failed");
            return Observable.error(new ExceptionWithResourceString(R.string.error_step5_reason));
        }*/

        return Observable.create(emitter -> {
            Timber.d("CheckingIfTheCredentialHasBeenRevoked - success");
            emitter.onNext("");
        }, Emitter.BackpressureMode.DROP).delay(delayTime, TimeUnit.SECONDS);
    }

    public Observable<Object> CheckingExpirationDate(BlockCert certificate, TxRecord txRecord, IssuerResponse issuerResponse) {

        return Observable.create(emitter -> {

            // This check is pretty simple (as implemented in the javascript verifier), just check the certificate's
            // expiration date against the current device datetime. Personally i think we shouldn't trust the device
            // time and should probably contact an NTP server.
            DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
            DateTime expiryDate = dateTimeFormatter.parseDateTime(certificate.getExpirationDate());
            DateTime currentDate = new DateTime();
            if (currentDate.isAfter(expiryDate)) {
                Timber.d("CheckingExpirationDate - failed");
                emitter.onError(new ExceptionWithResourceString(R.string.error_step6_reason));
                return;
            }


            Timber.d("CheckingExpirationDate - success");
            emitter.onNext("");
        }, Emitter.BackpressureMode.DROP).delay(delayTime, TimeUnit.SECONDS);
    }





    private void configureWebView(String serializedDoc, File file, HashComparison jsonldCallback) {
        mWebView.addJavascriptInterface(jsonldCallback, "jsonldCallback");
        WebSettings webSettings = mWebView.getSettings();
        // Enable JavaScript
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///" + file.getAbsolutePath());
    }

    private static class HashComparison {
        private final Emitter<Object> mEmitter;
        private final String mRemoteHash;

        public HashComparison(Emitter<Object> emitter, String remoteHash) {
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

}
