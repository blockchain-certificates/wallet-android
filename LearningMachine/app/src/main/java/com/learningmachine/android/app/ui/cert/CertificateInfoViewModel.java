package com.learningmachine.android.app.ui.cert;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Certificate;

public class CertificateInfoViewModel extends BaseObservable {

    private Certificate mCertificate;

    public CertificateInfoViewModel(Certificate certificate) {
        mCertificate = certificate;
    }

    @Bindable
    public String getIssuer() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getName();
    }

    @Bindable
    public String getDate() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getUuid(); //TODO: Change to get Document-> Assertion -> getIssuedOn
    }
}
