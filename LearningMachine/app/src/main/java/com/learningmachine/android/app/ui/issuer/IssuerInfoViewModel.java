package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.IssuerInfo;

public class IssuerInfoViewModel extends BaseObservable {

    private IssuerInfo mIssuerInfo;

    @Bindable
    public String getDate() {
        if (mIssuerInfo == null) {
            return null;
        }
        return mIssuerInfo.getDate();
    }

    @Bindable
    public String getSharedAddress() {
        if (mIssuerInfo == null) {
            return null;
        }
        return mIssuerInfo.getSharedAddress();
    }

    @Bindable
    public String getUrl() {
        if (mIssuerInfo == null) {
            return null;
        }
        return mIssuerInfo.getUrl();
    }

    @Bindable
    public String getEmail() {
        if (mIssuerInfo == null) {
            return null;
        }
        return mIssuerInfo.getEmail();
    }

    @Bindable
    public String getDescription() {
        if (mIssuerInfo == null) {
            return null;
        }
        return mIssuerInfo.getDescription();
    }

    public void bindIssuerInfo(IssuerInfo issuerInfo) {
        mIssuerInfo = issuerInfo;
        notifyChange();
    }

    public IssuerInfo getIssuerInfo() {
        return mIssuerInfo;
    }
}
