package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

public class LMDocument {
    @SerializedName("signature")
    private String mSignature;
    @SerializedName("recipient")
    private Recipient mRecipient;
    @SerializedName("@context")
    private String mDocumentResponseContext;
    @SerializedName("type")
    private String mType;
    @SerializedName("assertion")
    private LMAssertion mLMAssertion;
    @SerializedName("certificate")
    private Certificate mCertificate;
    @SerializedName("verify")
    private Verify mVerify;
}
