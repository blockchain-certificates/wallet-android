package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.learningmachine.android.app.R;
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

    public String getNumberOfCertificatesAsString(Context context) {
        if (mIssuer == null) {
            return null;
        }

        Resources resources = context.getResources();

        if (mIssuer.cachedNumberOfCertificatesForIssuer == 0) {
            return resources.getString(R.string.issuer_no_certificates_desc);
        }

        return resources.getQuantityString(R.plurals.certificate_counting,
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
