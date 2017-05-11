package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Issuer;

public class IssuerInfoViewModel extends BaseObservable {

    private Issuer mIssuer;

    public IssuerInfoViewModel(Issuer issuer) {
        mIssuer = issuer;
    }

    @Bindable
    public String getDate() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getPublicKey().getCreatedDate();
    }

    @Bindable
    public String getSharedAddress() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getPublicKey().getKey();
    }

    @Bindable
    public String getUrl() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getUuid();
    }

    @Bindable
    public String getEmail() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getEmail();
    }

    @Bindable
    public String getDescription() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName(); // TODO: confirm whether there's a separate description field
    }
}
