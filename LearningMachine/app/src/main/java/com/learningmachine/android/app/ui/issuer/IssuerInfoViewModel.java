package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Issuer;

public class IssuerInfoViewModel extends BaseObservable {

    private Issuer mIssuer;
    private String mIntroDate;
    private String mSharedAddress;

    public IssuerInfoViewModel(Issuer issuer, String introDate, String sharedAddress) {
        mIssuer = issuer;
        mIntroDate = introDate;
        mSharedAddress = sharedAddress;
    }

    @Bindable
    public String getDate() {
        return mIntroDate;
    }

    @Bindable
    public String getSharedAddress() {
        return mSharedAddress;
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
