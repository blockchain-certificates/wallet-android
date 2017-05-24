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
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.TxRecordOut;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.FileUtils;
import com.learningmachine.android.app.util.ListUtils;

import org.bitcoinj.core.NetworkParameters;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final NetworkParameters mNetworkParameters;

    @Inject
    public CertificateVerifier(Context context, BlockchainService blockchainService, IssuerService issuerService, NetworkParameters networkParameters) {
        mContext = context;
        mBlockchainService = blockchainService;
        mIssuerService = issuerService;
        mNetworkParameters = networkParameters;
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

    private Observable<BlockCert> parseCertificate(String string) {
        // TODO: use Gson that understands how to handle BlockCert v1.2 and v2.0
        return Observable.just(new Gson().fromJson(string, BlockCertV12.class));
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

    public Observable<String> verifyBitcoinTransactionRecord(BlockCert certificate) {
        String sourceId = certificate.getSourceId();
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

    private Observable<String> blockchainDownloaded(String remoteHash, BlockCert certificate) {
        String merkleRoot = certificate.getMerkleRoot();

        if (!remoteHash.equals(merkleRoot)) {
            // TODO: show an error
            Timber.d("The transaction record hash doesn't match the certificate's Merkle root");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }

        Timber.d("Blockchain transaction is downloaded successfully");
        return Observable.just(remoteHash);
    }

    public Observable<String> verifyIssuer(BlockCert certificate) {
        String issuerId = certificate.getIssuerId();
        return mIssuerService.getIssuer(issuerId)
                .flatMap(issuerResponse -> issuerDownloaded(issuerResponse, certificate));
    }

    private Observable<String> issuerDownloaded(IssuerResponse issuerResponse, BlockCert certificate) {
        List<KeyRotation> issuerKeys = issuerResponse.getIssuerKeys();
        if (ListUtils.isEmpty(issuerKeys)) {
            // TODO: show an error
            Timber.d("Issuer is missing keys");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }

        KeyRotation firstIssuerKey = issuerKeys.get(0);
        String address = certificate.getAddress(mNetworkParameters);
        if (address == null || !firstIssuerKey.getKey().equals(address)) {
            // TODO: show an error
            Timber.d("The issuer key doesn't match the certificate address");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
        return Observable.just(firstIssuerKey.getKey());
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
        BlockCert mCertificate;
        String mDocument;

        public CertificateAndDocument(BlockCert certificate, String document) {
            mCertificate = certificate;
            mDocument = document;
        }

        public BlockCert getCertificate() {
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
