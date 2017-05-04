package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bolot on 5/3/17.
 */

public class Document {
    @SerializedName("@context")
    private String mContext;
    @SerializedName("assertion")
    private LMAssertion mAssertion;
    @SerializedName("signature")
    private String mSignature;

    public LMAssertion getAssertion() {
        return mAssertion;
    }

    public String getSignature() {
        return mSignature;
    }
}
