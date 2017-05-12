package com.learningmachine.android.app.data;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.Document;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.Receipt;
import com.learningmachine.android.app.data.model.TxRecordOut;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.FileUtils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import javax.inject.Inject;

import rx.Emitter;
import rx.Observable;
import timber.log.Timber;

public class CertificateVerifier {
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
            String jsonString = scanner.useDelimiter("\\A").next();
            scanner.close();

            return Observable.just(jsonString);
        } catch (IOException e) {
            Timber.e(e, "Could not read certificate file");
            return Observable.error(e);
        }
    }

    private Observable<Certificate> parseCertificate(String string) {
        return Observable.just(new Gson().fromJson(string, Certificate.class));
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

    public Observable<String> verifyBitcoinTransactionRecord(Certificate certificate) {
        Receipt receipt = certificate.getReceipt();
        if (receipt == null) {
            // TODO: show an error
            Timber.d("Certificate receipt missing");
            return Observable.error(new Exception());
        }
        String sourceId = receipt.getFirstAnchorSourceId();
        return getBitcoinTransactionRecordHash(sourceId)
                .flatMap(remoteHash -> blockchainDownloaded(remoteHash, certificate));
    }

    public Observable<String> getBitcoinTransactionRecordHash(String sourceId) {
        return mBlockchainService.getBlockchain(sourceId)
                .flatMap(txRecord -> {
                    TxRecordOut txRecordOut = txRecord.getLastOut();
                    if (txRecordOut == null) {
                        return Observable.error(new Exception());
                    }
                    int value = txRecordOut.getValue();
                    if (value != 0) {
                        return Observable.error(new Exception());
                    }
                    String remoteHash = txRecordOut.getScript();
                    // strip out 6a20 prefix, if present
                    remoteHash = remoteHash.startsWith("6a20") ? remoteHash.substring(4) : remoteHash;
                    return Observable.just(remoteHash);
                });
    }

    private Observable<String> blockchainDownloaded(String remoteHash, Certificate certificate) {
        String merkleRoot = certificate.getReceipt().getMerkleRoot();

        if (!remoteHash.equals(merkleRoot)) {
            // TODO: show an error
            Timber.d("The transaction record hash doesn't match the certificate's Merkle root");
            return Observable.error(new Exception());
        }

        Timber.d("Blockchain transaction is downloaded successfully");
        return Observable.just(remoteHash);
    }

    public Observable<String> verifyIssuer(Certificate certificate) {
        String issuerUuid = certificate.getIssuerUuid();
        return mIssuerService.getIssuer(issuerUuid)
                .flatMap(issuerResponse -> issuerDownloaded(issuerResponse, certificate));
    }

    private Observable<String> issuerDownloaded(IssuerResponse issuerResponse, Certificate certificate) {
        if (issuerResponse.getIssuerKeys().isEmpty()) {
            // TODO: show an error
            Timber.d("Issuer is missing keys");
            return Observable.error(new Exception());
        }

        KeyRotation firstIssuerKey = issuerResponse.getIssuerKeys().get(0);

        Document document = certificate.getDocument();
        String signature = document.getSignature();
        String uuid = document.getAssertion().getUuid();

        try {
            ECKey ecKey = ECKey.signedMessageToKey(uuid, signature);
            ecKey.verifyMessage(uuid, signature); // this is tautological
            Address address = ecKey.toAddress(MainNetParams.get());
            if (!firstIssuerKey.getKey().equals(address.toBase58())) {
                // TODO: show an error
                Timber.d("The issuer key doesn't match the certificate address");
                return Observable.error(new Exception());
            }

            Timber.d("Issuer matches certificate");
            return Observable.just(firstIssuerKey.getKey());
        } catch (SignatureException e) {
            Timber.e(e);
            // TODO: show an error
            Timber.d("The document signature is invalid");
            return Observable.error(new Exception());
        }
    }

    public Observable<String> verifyJsonLd(String remoteHash, String serializedDoc) {
        Handler handler = new Handler();
        return Observable.fromEmitter(emitter -> {
            Object jsonldCallback = new Object() {
                @JavascriptInterface
                public void result(Object error, String normalizedJsonld) {
                    if (error != null) {
                        Timber.e(new Exception(), "Could not normalize JSON-LD");
                        emitter.onError(new Exception());
                        return;
                    }
                    Timber.d("Got the callback!");
                    MessageDigest sha256 = null;
                    try {
                        sha256 = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        Timber.e(e, "Couldn't obtain SHA-256, the impossible situation");
                        emitter.onError(new Exception());
                        return;
                    }
                    String localHash = String.format("%032x", new BigInteger(1, sha256.digest(normalizedJsonld.getBytes())));
                    if (localHash.equals(remoteHash)) {
                        emitter.onNext(localHash);
                    } else {
                        emitter.onError(new Exception());
                    }
                }
            };
            handler.post(() -> {
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
            });
        }, Emitter.BackpressureMode.DROP);
    }

    public static class CertificateAndDocument {
        Certificate mCertificate;
        String mDocument;

        public CertificateAndDocument(Certificate certificate, String document) {
            mCertificate = certificate;
            mDocument = document;
        }

        public Certificate getCertificate() {
            return mCertificate;
        }

        public String getDocument() {
            return mDocument;
        }
    }
}
