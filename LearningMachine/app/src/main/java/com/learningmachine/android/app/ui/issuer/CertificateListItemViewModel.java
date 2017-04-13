package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Certificate;

public class CertificateListItemViewModel extends BaseObservable {

    private Certificate mCertificate;

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

    public void bindCertificate(Certificate certificate) {
        mCertificate = certificate;
        notifyChange();
    }

    public Certificate getCertificate() {
        return mCertificate;
    }
}
