package com.learningmachine.android.app.ui.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.Issuer;

public class IssuerListItemViewModel extends BaseObservable {

    private Issuer mIssuer;

    public IssuerListItemViewModel() {
    }

    @Bindable
    public String getTitle() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    public void bindIssuer(Issuer issuer) {
        mIssuer = issuer;
        notifyChange();
    }

    public Issuer getIssuer() {
        return mIssuer;
    }
}
