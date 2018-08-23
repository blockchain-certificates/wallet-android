package com.learningmachine.android.app.ui.cert;

public class CertificateInfoItemViewModel {
    private String mTitle;
    private String mValue;

    public CertificateInfoItemViewModel(String title, String value) {
        mTitle = title;
        mValue = value;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }
}
