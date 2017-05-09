package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class Recipient {
    @SerializedName("givenName")
    private String mGivenName;
    @SerializedName("publicKey")
    private String mPublicKey;
    @SerializedName("type")
    private String mType;
    @SerializedName("identity")
    private String mIdentity;
    @SerializedName("familyName")
    private String mFamilyName;
    @SerializedName("revocationKey")
    private String mRevocationKey;
    @SerializedName("hashed")
    private boolean mHashed;

    public String getPublicKey() {
        return mPublicKey;
    }
}
