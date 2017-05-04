package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class LMAssertion {
    @SerializedName("issuedOn")
    private String mIssuedOn;
    @SerializedName("id")
    private String mId;
    @SerializedName("image:signature")
    private String mImageSignature;
    @SerializedName("type")
    private String mType;
    @SerializedName("uid")
    private String mUuid;

    public String getIssuedOn() {
        return mIssuedOn;
    }

    public String getId() {
        return mId;
    }

    public String getImageSignature() {
        return mImageSignature;
    }

    public String getType() {
        return mType;
    }

    public String getUuid() {
        return mUuid;
    }
}
