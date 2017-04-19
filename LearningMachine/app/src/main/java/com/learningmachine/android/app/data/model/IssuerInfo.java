package com.learningmachine.android.app.data.model;

import java.io.Serializable;

public class IssuerInfo implements Serializable {

    private String mDate;
    private String mSharedAddress;
    private String mUrl;
    private String mEmail;
    private String mDescription;

    public IssuerInfo(String date, String sharedAddress, String url, String email, String description) {
        mDate = date;
        mSharedAddress = sharedAddress;
        mUrl = url;
        mEmail = email;
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public String getSharedAddress() {
        return mSharedAddress;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getDescription() {
        return mDescription;
    }
}
