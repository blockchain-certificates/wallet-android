package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.CertificateRecord;

public class CertificateListItemViewModel extends BaseObservable {

    private CertificateRecord mCertificate;

    @Bindable
    public String getTitle() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getName();
    }

    @Bindable
    public String getDescription() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getDescription();
    }

    public void bindCertificate(CertificateRecord certificate) {
        mCertificate = certificate;
        notifyChange();
    }

    public CertificateRecord getCertificate() {
        return mCertificate;
    }
}
