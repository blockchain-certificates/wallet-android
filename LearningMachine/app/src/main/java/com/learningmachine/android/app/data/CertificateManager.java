package com.learningmachine.android.app.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.learningmachine.android.app.LMConstants;
import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.error.CertificateFileImportException;
import com.learningmachine.android.app.data.error.CertificateOwnershipException;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
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
import timber.log.Timber;

public class CertificateManager {

    private final Context mContext;
    private final CertificateStore mCertificateStore;
    private final IssuerStore mIssuerStore;
    private final CertificateService mCertificateService;
    private final BitcoinManager mBitcoinManager;
    private final IssuerManager mIssuerManager;

    public CertificateManager(Context context, CertificateStore certificateStore,
                              IssuerStore issuerStore, CertificateService certificateService,
                              BitcoinManager bitcoinManager, IssuerManager issuerManager) {
        mContext = context;
        mCertificateStore = certificateStore;
        mIssuerStore = issuerStore;
        mCertificateService = certificateService;
        mBitcoinManager = bitcoinManager;
        mIssuerManager = issuerManager;
    }

    public Observable<String> loadSampleCertificate() {
        AssetManager assetManager = mContext.getAssets();
        try (InputStream inputStream = assetManager.open("sample-certificate.json")) {
            return handleCertificateInputStream(inputStream);
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
        return mCertificateService.getCertificate(url)
                .flatMap(responseBody -> handleCertificateResponse(responseBody));
    }

    public Observable<String> addCertificate(File file) {
        return handleCertificateFile(file);
    }

    public Observable<Boolean> removeCertificate(String uuid) {
        return Observable.just(FileUtils.deleteCertificate(mContext, uuid))
                .map(success -> mCertificateStore.deleteCertificate(uuid));
    }

    /**
     * @param responseBody   Unparsed certificate response json
     * @return Error if save was unsuccessful
     */
    private Observable<String> handleCertificateResponse(ResponseBody responseBody) {
        try {
            // Copy the responseBody bytes before Gson consumes it
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer()
                    .clone();

            // Parse
            BlockCertParser blockCertParser = new BlockCertParser();
            BlockCert blockCert = blockCertParser.fromJson(responseBody.string());
            if (blockCert == null) {
                return Observable.error(new ExceptionWithResourceString(R.string.invalid_certificate));
            }
            String recipientKey = blockCert.getRecipientPublicKey();

            if (LMConstants.SHOULD_PERFORM_OWNERSHIP_CHECK) {
                // Reject on address mismatch
                boolean isSampleCert = recipientKey.equals("sample-certificate");
                // TODO: certificate ownership check must compare against all addresses
                // recipient key must match one of the bitcoin addresses
                boolean isCertOwner = mBitcoinManager.isMyIssuedAddress(recipientKey);

                if (!isSampleCert && !isCertOwner) {
                    return Observable.error(new CertificateOwnershipException());
                }
            }

            // Save to DB
            saveBlockCert(blockCert);

            // Write response to file
            String certUid = blockCert.getCertUid();
            FileUtils.saveCertificate(mContext, buffer, certUid);

            return mIssuerManager.fetchIssuer(blockCert.getIssuerId()).map(issuer -> {
                String recipientPublicKey = blockCert.getRecipientPublicKey();
                mIssuerStore.saveIssuerResponse(issuer, recipientPublicKey);
                return certUid;
            });
        } catch (JsonSyntaxException e) {
            Timber.w(e, "Certificate failed to parse");
            return Observable.error(e);
        } catch (IOException e) {
            Timber.e(e, "Couldn't save certificate");
            return Observable.error(e);
        }
    }

    private Observable<String> handleCertificateFile(File certificateFile) {
        try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
            return handleCertificateInputStream(inputStream);
        } catch (IOException | JsonParseException e) {
            return Observable.error(e);
        }
    }

    private Observable<String> handleCertificateInputStream(InputStream certInputStream) {
        // copy to temp file
        String tempFilename = "temp-cert";
        FileUtils.copyCertificateStream(mContext, certInputStream, tempFilename);

        BlockCert blockCert;
        File tempFile = FileUtils.getCertificateFile(mContext, tempFilename);
        try(FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            BlockCertParser blockCertParser = new BlockCertParser();
            blockCert = blockCertParser.fromJson(fileInputStream);
        } catch (IOException e) {
            Timber.e(e, "Unable to parse temp cert file");
            FileUtils.deleteCertificate(mContext, tempFilename);
            return Observable.error(e);
        }

        if (blockCert == null) {
            Timber.e("Failed to load a certificate from file. Data is null");
            return Observable.error(new CertificateFileImportException());
        }

        String recipientKey = blockCert.getRecipientPublicKey();

        if (LMConstants.SHOULD_PERFORM_OWNERSHIP_CHECK) {
            // Reject on address mismatch
            boolean isMyKey = mBitcoinManager.isMyIssuedAddress(recipientKey);
            if (!isMyKey) {
                FileUtils.deleteCertificate(mContext, tempFilename);
                return Observable.error(new CertificateOwnershipException());
            }
        }

        // Save to DB
        saveBlockCert(blockCert);

        // Rename / move temp file
        String certUid = blockCert.getCertUid();
        FileUtils.renameCertificateFile(mContext, tempFilename, certUid);

        return mIssuerManager.fetchIssuer(blockCert.getIssuerId()).map(issuer -> {
            String recipientPublicKey = blockCert.getRecipientPublicKey();
            mIssuerStore.saveIssuerResponse(issuer, recipientPublicKey);
            return certUid;
        });
    }

    private void saveBlockCert(BlockCert blockCert) {
        Timber.i("Saving certificate " + blockCert.getCertName());
        mCertificateStore.saveBlockchainCertificate(blockCert);
    }
}
