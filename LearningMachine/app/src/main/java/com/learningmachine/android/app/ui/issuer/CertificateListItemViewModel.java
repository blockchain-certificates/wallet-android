package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuingEstimate;

public class CertificateListItemViewModel extends BaseObservable {

    private CertificateRecord mCertificate;
    private IssuingEstimate mEstimate;

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
        mEstimate = null;
        notifyChange();
    }

    public void bindEstimate(IssuingEstimate estimate) {
        mCertificate = null;
        mEstimate = estimate;
        notifyChange();
    }

    public CertificateRecord getCertificate() {
        return mCertificate;
    }
    public IssuingEstimate getIssuingEstimate() { return mEstimate; }
}
