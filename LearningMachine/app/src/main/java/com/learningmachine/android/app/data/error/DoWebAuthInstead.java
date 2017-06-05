package com.learningmachine.android.app.data.error;

public class DoWebAuthInstead extends Exception {
    private final String mIntroUrl;
    private final String mSuccessUrl;
    private final String mErrorUrl;

    public DoWebAuthInstead(String introUrl, String successUrl, String errorUrl) {
        mIntroUrl = introUrl;
        mSuccessUrl = successUrl;
        mErrorUrl = errorUrl;
    }

    public String getIntroUrl() {
        return mIntroUrl;
    }

    public String getSuccessUrl() {
        return mSuccessUrl;
    }

    public String getErrorUrl() {
        return mErrorUrl;
    }
}
