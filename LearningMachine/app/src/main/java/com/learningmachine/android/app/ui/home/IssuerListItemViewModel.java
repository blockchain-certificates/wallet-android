package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;

import com.learningmachine.android.app.data.model.Issuer;

public class IssuerListItemViewModel extends BaseObservable {

    private Context mContext;
    private Issuer mIssuer;

    public IssuerListItemViewModel(Context context) {
        mContext = context;
    }

    @Bindable
    public String getTitle() {
        if (mIssuer == null) {
            return null;
        }
        return mIssuer.getName();
    }

    @Bindable
    public Drawable getImage() {
        int imageResId = getImageResId();
        return ContextCompat.getDrawable(mContext, imageResId);
    }

    @VisibleForTesting
    int getImageResId() {
        if (mIssuer == null) {
            return 0;
        }
        // TODO fix
        return 0;
    }

    public void bindIssuer(Issuer issuer) {
        mIssuer = issuer;
        notifyChange();
    }

    public Issuer getIssuer() {
        return mIssuer;
    }
}
