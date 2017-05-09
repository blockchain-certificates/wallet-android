package com.learningmachine.android.app.ui.cert;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Certificate;

public class CertificateInfoViewModel extends BaseObservable {

    private Certificate mCertificate;

    public CertificateInfoViewModel (Certificate certificate) {
        mCertificate = certificate;
    }

    @Bindable
    public String getIssuer() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getIssuerResponse().getName();
    }
    @Bindable
    public String getDate() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getLanguage(); //TODO: Change to get Document-> Assertion -> getIssuedOn
    }
}
