package com.learningmachine.android.app.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.JsonSyntaxException;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.error.CertificateOwnershipException;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import rx.Observable;

public class CertificateManager {

    private final Context mContext;
    private final CertificateStore mCertificateStore;
    private final IssuerStore mIssuerStore;
    private final CertificateService mCertificateService;
    private final BitcoinManager mBitcoinManager;

    public CertificateManager(Context context, CertificateStore certificateStore, IssuerStore issuerStore, CertificateService certificateService, BitcoinManager bitcoinManager) {
        mContext = context;
        mCertificateStore = certificateStore;
        mIssuerStore = issuerStore;
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
            BlockCertParser blockCertParser = new BlockCertParser();
            BlockCert blockCert = blockCertParser.fromJson(responseBody.string());
            String recipientKey = blockCert.getRecipientPublicKey();

            // Reject on address mismatch
            boolean isSampleCert = recipientKey.equals("sample-certificate");
            boolean isCertOwner = bitcoinAddress.equals(recipientKey);

            if (!isSampleCert && !isCertOwner) {
                return Observable.error(new CertificateOwnershipException());
            }

            // Save to DB
            saveBlockCert(blockCert);

            // Write response to file
            String certUid = blockCert.getCertUid();
            FileUtils.saveCertificate(mContext, buffer, certUid);
            return Observable.just(certUid);
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
        BlockCertParser blockCertParser = new BlockCertParser();
        BlockCert blockCert = blockCertParser.fromJson(certInputStream);
        String recipientKey = blockCert.getRecipientPublicKey();

        // Reject on address mismatch
        if (!bitcoinAddress.equals(recipientKey)) {
            return Observable.error(new CertificateOwnershipException());
        }

        // Save to DB
        saveBlockCert(blockCert);

        // Copy file
        String certUid = blockCert.getCertUid();
        FileUtils.copyCertificateStream(mContext, certInputStream, certUid);
        return Observable.just(certUid);
    }

    private void saveBlockCert(BlockCert blockCert) {
        mCertificateStore.saveBlockchainCertificate(blockCert);
        IssuerRecord issuer = blockCert.getIssuer();
        mIssuerStore.saveIssuer(issuer);
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
}
