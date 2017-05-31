package com.learningmachine.android.app.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

import org.bitcoinj.core.NetworkParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import timber.log.Timber;

public class CertificateVerifier {
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
        String sourceId = certificate.getSourceId();
        return mBlockchainService.getBlockchain(sourceId)
                .flatMap(txRecord -> verifyBitcoinTransactionRecord(certificate, txRecord));
    }

    private Observable<TxRecord> verifyBitcoinTransactionRecord(BlockCert certificate, TxRecord txRecord) {
        String merkleRoot = certificate.getMerkleRoot();
        String remoteHash = txRecord.getRemoteHash();

        if (remoteHash == null || !remoteHash.equals(merkleRoot)) {
            // TODO: show an error
            Timber.e("The transaction record hash doesn't match the certificate's Merkle root");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }

        Timber.d("Blockchain transaction is downloaded successfully");
        return Observable.just(txRecord);
    }

    public Observable<IssuerResponse> verifyIssuer(BlockCert certificate, TxRecord txRecord) {
        String issuerId = certificate.getIssuerId();
        return mIssuerService.getIssuer(issuerId)
                .flatMap(issuerResponse -> verifyIssuer(certificate, issuerResponse, txRecord));
    }

    private Observable<IssuerResponse> verifyIssuer(BlockCert certificate, IssuerResponse issuerResponse, TxRecord txRecord) {
        boolean addressVerified = issuerResponse.verifyTransaction(txRecord);
        if (!addressVerified) {
            // TODO: show an error
            Timber.e("The issuer key doesn't match the certificate address");
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
        return Observable.just(issuerResponse);
    }

    public Observable<String> verifyJsonLd(BlockCert certificate, TxRecord txRecord) {
        String remoteHash = txRecord.getRemoteHash();
        JsonObject canonicalizedJson = certificate.getCanonicalizedJson();
        if (canonicalizedJson == null) {
            return Observable.error(new ExceptionWithResourceString(R.string.error_invalid_certificate_json));
        }
        String serializedDoc = canonicalizedJson.toString();
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
                Timber.e(e, String.format("Remote hash [%s] does not match local hash [%s]", mRemoteHash, localHash));
                mEmitter.onError(e);
            }
        }
    }
}
