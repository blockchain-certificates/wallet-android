package com.learningmachine.android.app.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.v12.BlockchainCertificate;
import com.learningmachine.android.app.data.cert.v12.Document;
import com.learningmachine.android.app.data.cert.v12.Receipt;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecordOut;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.FileUtils;
import com.learningmachine.android.app.util.ListUtils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import timber.log.Timber;

public class CertificateVerifier {
    private static final String BEGINNING_OF_INPUT = "\\A"; // see Pattern docs
    private static final String OP_RETURN_PREFIX = "6a20";

    private final WebView mWebView;
    private Context mContext;
    private BlockchainService mBlockchainService;
    private IssuerService mIssuerService;

    @Inject
    public CertificateVerifier(Context context, BlockchainService blockchainService, IssuerService issuerService) {
        mContext = context;
        mBlockchainService = blockchainService;
        mIssuerService = issuerService;
        mWebView = new WebView(mContext);
    }

    public Observable<CertificateAndDocument> loadCertificate(String certificateUuid) {
        return getCertificateFileContents(mContext, certificateUuid)
                .flatMap(string -> Observable.combineLatest(parseCertificate(string), getCertificateDocument(string), CertificateAndDocument::new));
    }

    private Observable<String> getCertificateFileContents(Context context, String certificateUuid) {
        File file = FileUtils.getCertificateFile(context, certificateUuid);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Scanner scanner = new Scanner(inputStream);
            String jsonString = scanner.useDelimiter(BEGINNING_OF_INPUT).next();
            scanner.close();

            return Observable.just(jsonString);
        } catch (IOException e) {
            Timber.e(e, "Could not read certificate file");
            return Observable.error(e);
        }
    }

    private Observable<BlockchainCertificate> parseCertificate(String string) {
        return Observable.just(new Gson().fromJson(string, BlockchainCertificate.class));
    }

    private Observable<String> getCertificateDocument(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject document = jsonObject.getJSONObject("document");
            String serializedDoc = document.toString();

            return Observable.just(serializedDoc);
        } catch (JSONException e) {
            Timber.e(e, "Couldn't parse certificate JSON");
            return Observable.error(e);
        }
    }

    public Observable<String> verifyBitcoinTransactionRecord(BlockchainCertificate certificate) {
        Receipt receipt = certificate.getReceipt();
        if (receipt == null || ListUtils.isEmpty(receipt.getAnchors())) {
            // TODO: show an error
            Timber.d("Certificate receipt missing");
            Exception e = new ExceptionWithResourceString(R.string.error_invalid_certificate_json);
            return Observable.error(e);
        }
        String sourceId = receipt.getAnchors().get(0).getSourceId();
        return getBitcoinTransactionRecordHash(sourceId)
                .flatMap(remoteHash -> blockchainDownloaded(remoteHash, certificate));
    }

    private Observable<String> getBitcoinTransactionRecordHash(String sourceId) {
        return mBlockchainService.getBlockchain(sourceId)
                .flatMap(txRecord -> {
                    TxRecordOut txRecordOut = txRecord.getLastOut();
                    if (txRecordOut == null) {
                        return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
                    }
                    int value = txRecordOut.getValue();
                    if (value != 0) {
                        return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
                    }
                    String remoteHash = txRecordOut.getScript();
                    // strip out 6a20 prefix, if present
                    remoteHash = remoteHash.startsWith(OP_RETURN_PREFIX) ? remoteHash.substring(4) : remoteHash;
                    return Observable.just(remoteHash);
                });
    }

    private Observable<String> blockchainDownloaded(String remoteHash, BlockchainCertificate certificate) {
        Receipt receipt = certificate.getReceipt();
        if (receipt == null) {
            Timber.e("The receipt is missing from the certificate");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
        String merkleRoot = receipt.getMerkleRoot();

        if (!remoteHash.equals(merkleRoot)) {
            // TODO: show an error
            Timber.d("The transaction record hash doesn't match the certificate's Merkle root");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }

        Timber.d("Blockchain transaction is downloaded successfully");
        return Observable.just(remoteHash);
    }

    public Observable<String> verifyIssuer(BlockchainCertificate certificate) {
        URI issuerUuid = certificate.getDocument().getCertificate().getIssuer().getId();
        return mIssuerService.getIssuer(issuerUuid.toString())
                .flatMap(issuerResponse -> issuerDownloaded(issuerResponse, certificate));
    }

    private Observable<String> issuerDownloaded(IssuerResponse issuerResponse, BlockchainCertificate certificate) {
        List<KeyRotation> issuerKeys = issuerResponse.getIssuerKeys();
        if (ListUtils.isEmpty(issuerKeys)) {
            // TODO: show an error
            Timber.d("Issuer is missing keys");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }

        KeyRotation firstIssuerKey = issuerKeys.get(0);

        Document document = certificate.getDocument();
        String signature = document.getSignature();
        // TODO: check if we could get UUID from the cert
        String uid = document.getAssertion().getUid();

        try {
            ECKey ecKey = ECKey.signedMessageToKey(uid, signature);
            ecKey.verifyMessage(uid, signature); // this is tautological
            Address address = ecKey.toAddress(MainNetParams.get());
            if (!firstIssuerKey.getKey().equals(address.toBase58())) {
                // TODO: show an error
                Timber.d("The issuer key doesn't match the certificate address");
                return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
            }

            Timber.d("Issuer matches certificate");
            return Observable.just(firstIssuerKey.getKey());
        } catch (SignatureException e) {
            // TODO: show an error
            Timber.e(e, "The document signature is invalid");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
    }

    public Observable<String> verifyJsonLd(String remoteHash, String serializedDoc) {
        Handler handler = new Handler(Looper.getMainLooper());
        return Observable.fromEmitter(emitter -> {
            HashComparison jsonldCallback = new HashComparison(emitter, remoteHash);
            handler.post(() -> configureWebView(serializedDoc, jsonldCallback));
        }, Emitter.BackpressureMode.DROP);
    }

    private void configureWebView(String serializedDoc, HashComparison jsonldCallback) {
        String html = "<html><head><script src=\"https://cdnjs.cloudflare.com/ajax/libs/jsonld/0.4.12/jsonld.js\"></script></head></html>";
        mWebView.addJavascriptInterface(jsonldCallback, "jsonldCallback");
        WebSettings webSettings = mWebView.getSettings();
        // Enable JavaScript.
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String options = "{algorithm: 'URDNA2015', format: 'application/nquads'}";
                String jsResultHandler = "function(err, result) { jsonldCallback.result(err, result); }";
                String jsString = "(function() {jsonld.normalize(" + serializedDoc + ", " + options + ", " + jsResultHandler + ")})()";
                view.loadUrl("javascript:" + jsString);
            }
        });
        mWebView.loadData(html, "text/html", "UTF-8");
    }

    public static class CertificateAndDocument {
        BlockchainCertificate mCertificate;
        String mDocument;

        public CertificateAndDocument(BlockchainCertificate certificate, String document) {
            mCertificate = certificate;
            mDocument = document;
        }

        public BlockchainCertificate getCertificate() {
            return mCertificate;
        }

        public String getDocument() {
            return mDocument;
        }
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
                Exception e = new ExceptionWithResourceString(R.string.error_invalid_certificate_json);
                Timber.e(e, "Could not normalize JSON-LD");
                mEmitter.onError(e);
                return;
            }
            Timber.d("Got the callback!");
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
                Exception e = new ExceptionWithResourceString(R.string.error_invalid_certificate_json);
                mEmitter.onError(e);
            }
        }
    }
}
