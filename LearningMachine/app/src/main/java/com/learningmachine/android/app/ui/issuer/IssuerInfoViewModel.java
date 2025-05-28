package com.hyland.android.app.ui.issuer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hyland.android.app.data.model.IssuerRecord;
import com.hyland.android.app.util.DateUtils;

public class IssuerInfoViewModel extends BaseObservable {

    private IssuerRecord mIssuer;

    public IssuerInfoViewModel(IssuerRecord issuer) {
        mIssuer = issuer;
    }

    @Bindable
    public String getTitle() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    @Bindable
    public String getIssuerUrl() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getIssuerURL();
    }



    @Bindable
    public String getDate() {
        if (mIssuer == null) {
            return null;
        }
        String dateString = mIssuer.getIntroducedOn();
        return DateUtils.formatDateString(dateString);
    }

    @Bindable
    public String getSharedAddress() {
        return mIssuer.getRecipientPubKey();
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
