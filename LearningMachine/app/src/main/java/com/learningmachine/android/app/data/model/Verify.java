package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class Verify {
    @SerializedName("attribute-signed")
    private String mAttributeSigned;
    @SerializedName("type")
    private String mType;
    @SerializedName("signer")
    private String mSigner;
}
