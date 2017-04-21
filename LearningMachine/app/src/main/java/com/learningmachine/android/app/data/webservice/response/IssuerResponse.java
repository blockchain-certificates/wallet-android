package com.learningmachine.android.app.data.webservice.response;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.data.model.KeyRotation;

import java.util.List;

public class IssuerResponse {
    @SerializedName("name")
    private String mName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("id")
    private String mUuid;
    @SerializedName("url")
    private String mCertsUrl;
    @SerializedName("introductionURL")
    private String mIntroUrl;
    @SerializedName("issuerKeys")
    private List<KeyRotation> mIssuerKeys;
    @SerializedName("revocationKeys")
    private List<KeyRotation> mRevocationKeys;
    @SerializedName("image")
    private String mImageData;

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUuid() {
        return mUuid;
    }

    public String getCertsUrl() {
        return mCertsUrl;
    }

    public String getIntroUrl() {
        return mIntroUrl;
    }

    public List<KeyRotation> getIssuerKeys() {
        return mIssuerKeys;
    }

    public List<KeyRotation> getRevocationKeys() {
        return mRevocationKeys;
    }

    public String getImageData() {
        return mImageData;
    }
}