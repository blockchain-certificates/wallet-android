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

        // TODO: Move these to the strings.xml
        if(mIssuer.cachedNumberOfCertificatesForIssuer == 0){
            return "No Credentials";
        }
        if(mIssuer.cachedNumberOfCertificatesForIssuer == 1){
            return "1 Credential";
        }
        return String.format("%d Credentials", mIssuer.cachedNumberOfCertificatesForIssuer);
    }

    public void bindIssuer(IssuerRecord issuer) {
        mIssuer = issuer;
        notifyChange();
    }

    public IssuerRecord getIssuer() {
        return mIssuer;
    }
}
