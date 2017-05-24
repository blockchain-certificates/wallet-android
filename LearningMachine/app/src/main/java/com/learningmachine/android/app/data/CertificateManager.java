package com.learningmachine.android.app.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.cert.v12.BlockchainCertificate;
import com.learningmachine.android.app.data.cert.v12.Document;
import com.learningmachine.android.app.data.cert.v12.Recipient;
import com.learningmachine.android.app.data.error.CertificateOwnershipException;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import rx.Observable;

public class CertificateManager {

    private Context mContext;
    private CertificateStore mCertificateStore;
    private CertificateService mCertificateService;
    private BitcoinManager mBitcoinManager;

    public CertificateManager(Context context, CertificateStore certificateStore, CertificateService certificateService, BitcoinManager bitcoinManager) {
        mContext = context;
        mCertificateStore = certificateStore;
        mCertificateService = certificateService;
        mBitcoinManager = bitcoinManager;
    }

    public Observable<String> loadSampleCertificate() {
        AssetManager assetManager = mContext.getAssets();
        try (InputStream inputStream = assetManager.open("sample-certificate.json")) {
            return handleCertificateInputStream(inputStream, "sample-certificate");
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    public Observable<CertificateRecord> getCertificate(String certificateUuid) {
        return Observable.just(mCertificateStore.loadCertificate(certificateUuid));
    }

    public Observable<List<CertificateRecord>> getCertificatesForIssuer(String issuerUuid) {
        return Observable.just(mCertificateStore.loadCertificatesForIssuer(issuerUuid));
    }

    public Observable<String> addCertificate(String url) {
        return Observable.combineLatest(mCertificateService.getCertificate(url),
                mBitcoinManager.getBitcoinAddress(),
                AddCertificateHolder::new)
                .flatMap(holder -> handleCertificateResponse(holder.getResponseBody(), holder.getBitcoinAddress()));
    }

    public Observable<String> addCertificate(File file) {
        return mBitcoinManager.getBitcoinAddress()
                .flatMap(bitcoinAddress -> handleCertificateFile(file, bitcoinAddress));
    }

    public Observable<Boolean> removeCertificate(String uuid) {
        return Observable.just(FileUtils.deleteCertificate(mContext, uuid))
                .map(success -> mCertificateStore.deleteCertificate(uuid));
    }

    /**
     * @param responseBody   Unparsed certificate response json
     * @param bitcoinAddress Wallet receive address
     * @return Error if save was unsuccessful
     */
    private Observable<String> handleCertificateResponse(ResponseBody responseBody, String bitcoinAddress) {
        try {
            // Copy the responseBody bytes before Gson consumes it
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer()
                    .clone();

            // Parse
            Gson gson = new Gson();
            BlockchainCertificate certificate = gson.fromJson(responseBody.string(), BlockchainCertificate.class);
            Document document = certificate.getDocument();
            Recipient recipient = document.getRecipient();
            String recipientKey = recipient.getPublicKey();

            // Reject on address mismatch
            boolean isSampleCert = recipientKey.equals("sample-certificate");
            boolean isCertOwner = bitcoinAddress.equals(recipientKey);

            if (!isSampleCert && !isCertOwner) {
                return Observable.error(new CertificateOwnershipException());
            }

            // Save to DB
            mCertificateStore.saveBlockchainCertificate(certificate);

            // Write response to file
            String uuid = document.getAssertion().getUid();
            FileUtils.saveCertificate(mContext, buffer, uuid);
            return Observable.just(uuid);
        } catch (JsonSyntaxException | IOException e) {
            return Observable.error(e);
        }
    }

    private Observable<String> handleCertificateFile(File certificateFile, String bitcoinAddress) {
        try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
            return handleCertificateInputStream(inputStream, bitcoinAddress);
        } catch (IOException e) {
            return Observable.error(e);
        }
    }

    private Observable<String> handleCertificateInputStream(InputStream certInputStream, String bitcoinAddress) {
        Gson gson = new Gson();
        InputStreamReader inputStreamReader = new InputStreamReader(certInputStream);
        BlockchainCertificate blockchainCertificate = gson.fromJson(inputStreamReader, BlockchainCertificate.class);
        Document document = blockchainCertificate.getDocument();
        Recipient recipient = document.getRecipient();
        String recipientKey = recipient.getPublicKey();

        // Reject on address mismatch
        if (!bitcoinAddress.equals(recipientKey)) {
            return Observable.error(new CertificateOwnershipException());
        }

        // Save to DB
        mCertificateStore.saveBlockchainCertificate(blockchainCertificate);

        // Copy file
        String uuid = document.getAssertion().getUid();
        FileUtils.copyCertificateStream(mContext, certInputStream, uuid);
        return Observable.just(uuid);
    }

    static class AddCertificateHolder {
        private final ResponseBody mResponseBody;
        private final String mBitcoinAddress;

        AddCertificateHolder(ResponseBody responseBody, String bitcoinAddress) {
            mResponseBody = responseBody;
            mBitcoinAddress = bitcoinAddress;
        }

        ResponseBody getResponseBody() {
            return mResponseBody;
        }

        String getBitcoinAddress() {
            return mBitcoinAddress;
        }
    }

    public void purgeCertificates() {
        mCertificateStore.reset();
        loadSampleCertificate();
    }
}
