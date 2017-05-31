package com.learningmachine.android.app.data.error;

import android.support.annotation.StringRes;

public class ExceptionWithResourceString extends Exception {
    private @StringRes int mErrorMessageResId;

    public ExceptionWithResourceString(int errorMessageResId) {
        mErrorMessageResId = errorMessageResId;
    }

    public ExceptionWithResourceString(Throwable cause, int errorMessageResId) {
        super(cause);
        mErrorMessageResId = errorMessageResId;
    }

    public int getErrorMessageResId() {
        return mErrorMessageResId;
    }
}
