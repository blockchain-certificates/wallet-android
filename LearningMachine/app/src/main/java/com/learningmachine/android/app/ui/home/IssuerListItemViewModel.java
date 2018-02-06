package com.learningmachine.android.app.ui.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.IssuerRecord;

public class IssuerListItemViewModel extends BaseObservable {

    private IssuerRecord mIssuer;

    public IssuerListItemViewModel() {
    }

    @Bindable
    public String getTitle() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    @Bindable
    public String getNumberOfCertificatesAsString() {
        if (mIssuer == null) {
            return null;
        }

        return String.format("%d Certificates", mIssuer.cachedNumberOfCertificatesForIssuer);
    }

    public void bindIssuer(IssuerRecord issuer) {
        mIssuer = issuer;
        notifyChange();
    }

    public IssuerRecord getIssuer() {
        return mIssuer;
    }
}
