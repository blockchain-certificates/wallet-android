package com.learningmachine.android.app.ui.cert;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.util.DateUtils;

public class CertificateInfoViewModel extends BaseObservable {

    private Certificate mCertificate;
    private Issuer mIssuer;

    public CertificateInfoViewModel(Certificate certificate, Issuer issuer) {
        mCertificate = certificate;
        mIssuer = issuer;
    }

    @Bindable
    public String getIssuerName() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    @Bindable
    public String getDate() {
        if (mCertificate == null) {
            return null;
        }
        String dateString = mCertificate.getIssueOn();
        return DateUtils.formatDateString(dateString);
    }
}
