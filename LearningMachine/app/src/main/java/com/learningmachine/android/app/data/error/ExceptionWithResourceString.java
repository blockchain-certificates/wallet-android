package com.learningmachine.android.app.data.error;

import android.support.annotation.StringRes;

public class ExceptionWithResourceString extends Exception {
    private @StringRes int mErrorMessageResId;

    public ExceptionWithResourceString(int errorMessageResId) {
        mErrorMessageResId = errorMessageResId;
    }

    public int getErrorMessageResId() {
        return mErrorMessageResId;
    }
}
