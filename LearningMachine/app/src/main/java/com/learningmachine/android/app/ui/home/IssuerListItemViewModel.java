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

    private String mTitleString;
    private int mImageResId;

    public IssuerListItemViewModel(Context context) {
        mContext = context;
    }

    @Bindable
    public String getTitle() {
        return mTitleString;
    }

    @Bindable
    public Drawable getImage() {
        int imageResId = getImageResId();
        return ContextCompat.getDrawable(mContext, imageResId);
    }

    @VisibleForTesting
    int getImageResId() {
        return mImageResId;
    }

    public void bindIssuer(Issuer issuer) {
        mTitleString = issuer.getName();
        mImageResId = issuer.getImageResId();
        notifyChange();
    }
}
