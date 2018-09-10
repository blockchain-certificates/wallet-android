package com.learningmachine.android.app.ui.issuer;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.model.IssuerRecord;

public class CertificateHeaderViewModel extends BaseObservable {

    private IssuerRecord mIssuer;

    @Bindable
    public String getTitle() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    public boolean hasCertificate() {
        return mIssuer.cachedNumberOfCertificatesForIssuer > 0;
    }

    @Bindable
    public int getCertificateTitleVisibility(){
        if (mIssuer == null) {
            return View.GONE;
        }
        return mIssuer.cachedNumberOfCertificatesForIssuer > 0 ? View.VISIBLE : View.GONE;
    }

    public String getNumberOfCertificatesAsString(Context context) {
        if (mIssuer == null) {
            return null;
        }

        Resources resources = context.getResources();

        return resources.getQuantityString(R.plurals.certificate_counting_head,
                mIssuer.cachedNumberOfCertificatesForIssuer,
                mIssuer.cachedNumberOfCertificatesForIssuer);
    }

    public void bindIssuer(IssuerRecord issuer) {
        mIssuer = issuer;
        notifyChange();
    }

    public IssuerRecord getIssuer() {
        return mIssuer;
    }
}
