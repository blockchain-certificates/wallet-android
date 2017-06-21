package com.learningmachine.android.app.ui.issuer;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.util.DateUtils;

public class IssuerInfoViewModel extends BaseObservable {

    private IssuerRecord mIssuer;
    private String mBitcoinAddress;

    public IssuerInfoViewModel(IssuerRecord issuer, String bitcoinAddress) {
        mIssuer = issuer;
        mBitcoinAddress = bitcoinAddress;
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
        return mBitcoinAddress;
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
