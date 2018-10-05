package com.learningmachine.android.app.data;

import android.content.Context;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;


// Note: this class has been completely neutered and replaced with embedded javascript which does
// the actual certificate verification process.  Instead, this class now provides a
// theatrical playback of the verification process only
public class CertificateVerifier {
    private final Context mContext;

    @Inject
    public CertificateVerifier(Context context, BlockchainService blockchainService, IssuerService issuerService) {
        mContext = context;
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

}
