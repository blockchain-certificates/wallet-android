package com.learningmachine.android.app.ui.cert;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.util.DateUtils;

public class CertificateInfoViewModel extends BaseObservable {

    private CertificateRecord mCertificate;
    private IssuerRecord mIssuer;

    public CertificateInfoViewModel(CertificateRecord certificate, IssuerRecord issuer) {
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
        String dateString = mCertificate.getIssuedOn();
        return DateUtils.formatDateString(dateString);
    }
}
