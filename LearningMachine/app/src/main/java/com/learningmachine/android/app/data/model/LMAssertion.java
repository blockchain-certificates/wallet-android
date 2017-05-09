package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LMAssertion {
    @SerializedName("issuedOn")
    private String mIssuedOn;
    @SerializedName("id")
    private String mId;
    @SerializedName("image:signature")
    private List<ImageSignature> mImageSignatureList;
    @SerializedName("type")
    private String mType;
    @SerializedName("uid")
    private String mUid;

    public String getIssuedOn() {
        return mIssuedOn;
    }

    public String getId() {
        return mId;
    }

    public List<ImageSignature> getImageSignatureList() {
        return mImageSignatureList;
    }

    public String getType() {
        return mType;
    }

    public String getUuid() {
        return mUid;
    }
}
